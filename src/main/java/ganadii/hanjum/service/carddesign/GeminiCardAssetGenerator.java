package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.S3Service;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import ganadii.hanjum.service.carddesign.dto.GeminiApiResponse;
import ganadii.hanjum.util.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Google Gemini API를 사용한 실제 이미지 생성 Generator
 */
@Slf4j
@Component
@Primary
@Profile("!test")
@RequiredArgsConstructor
public class GeminiCardAssetGenerator implements CardAssetGenerator {

    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    @Value("${app.gemini.api-url:https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-image:generateContent}")
    private String geminiApiUrl;

    @Value("${app.gemini.api-key}")
    private String geminiApiKey;

    @Override
    public CardAssetDescriptor generate(CardDesignRequest request) {
        log.info("Generating card image via Gemini API: {}", request);

        try {
            // 1. S3 키 생성
            String s3Key = buildS3Key(request);

            // 2. Gemini API 호출
            byte[] imageBytes = callGeminiApi(request);

            // 3. S3 업로드
            String imageUrl = s3Service.upload(s3Key, imageBytes, "image/png");

            // 4. 체크섬 계산
            String checksum = CryptoUtils.sha256(imageBytes);

            log.info("Card image generated successfully: s3Key={}, url={}", s3Key, imageUrl);

            return new CardAssetDescriptor(imageUrl, s3Key, CardImageSource.GENERATED, checksum);

        } catch (Exception e) {
            log.error("Failed to generate card image", e);
            throw new RuntimeException("Failed to generate card image via Gemini API", e);
        }
    }

    /**
     * Gemini API 호출
     */
    private byte[] callGeminiApi(CardDesignRequest request) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            log.warn("No Gemini API key configured, using dummy image");
            return createDummyPngBytes();
        }

        try {
            // 1. 프롬프트 생성
            String prompt = buildPrompt(request);
            log.info("Generated prompt: {}", prompt);

            // 2. Request Body 구성
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(
                        Map.of("text", prompt)
                    ))
                ),
                "generationConfig", Map.of(
                    "responseModalities", List.of("Image")
                )
            );

            // 3. Headers 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-goog-api-key", geminiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 4. API 호출
            log.info("Calling Gemini API: {}", geminiApiUrl);
            ResponseEntity<GeminiApiResponse.Response> response = restTemplate.postForEntity(
                geminiApiUrl,
                entity,
                GeminiApiResponse.Response.class
            );

            // 5. Response 파싱
            GeminiApiResponse.Response body = response.getBody();
            if (body == null || body.candidates() == null || body.candidates().isEmpty()) {
                throw new RuntimeException("Empty or invalid response from Gemini API");
            }

            log.info("Gemini API response received with {} candidates", body.candidates().size());

            // Extract image data from first candidate
            GeminiApiResponse.Candidate candidate = body.candidates().get(0);
            if (candidate.content() == null || candidate.content().parts() == null) {
                throw new RuntimeException("No content in Gemini response");
            }

            // Find part with inline image data
            GeminiApiResponse.InlineData inlineData = candidate.content().parts().stream()
                    .map(GeminiApiResponse.Part::getInlineData)
                    .filter(data -> data != null && data.data() != null && !data.data().isEmpty())
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("No image data in Gemini response"));

            // 6. Base64 디코딩
            return Base64.getDecoder().decode(inlineData.data());

        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    /**
     * 프롬프트 생성
     */
    private String buildPrompt(CardDesignRequest request) {
        // 꽃 정보
        Flowers flower = request.mainFlower();
        String flowerName = flower.getEnglishName() != null
                ? flower.getEnglishName()
                : flower.getKoreanName();

        // 시나리오 정보
        String who = translateWhoType(request.whoType());
        String when = translateWhenTypes(request.whenTypes());
        String emotion = translateEmotionType(request.emotionType());
        String size = translateBouquetSize(request.bouquetSize());

        // 프롬프트 생성
        return String.format(
            "Create a beautiful flower bouquet image with the following specifications:\n" +
            "- Main Flower: %s\n" +
            "- Recipient: %s\n" +
            "- Occasion: %s\n" +
            "- Emotion/Mood: %s\n" +
            "- Size: %s\n" +
            "\n" +
            "IMPORTANT: Follow the EXACT SAME STYLE, composition, and visual aesthetic as the reference image provided.\n" +
            "Match the reference image's:\n" +
            "- Photography style and lighting\n" +
            "- Background treatment (color, gradient, texture)\n" +
            "- Bouquet arrangement style\n" +
            "- Overall mood and atmosphere\n" +
            "- Image quality and resolution\n" +
            "\n" +
            "Additional requirements:\n" +
            "- High quality, photorealistic style\n" +
            "- Professional florist arrangement\n" +
            "- Centered composition\n" +
            "- Natural lighting\n" +
            "- Fresh and vibrant colors\n" +
            "\n" +
            "The bouquet should convey %s feelings and be perfect for giving to %s on %s.",
            flowerName,
            who,
            when,
            emotion,
            size,
            emotion.toLowerCase(),
            who.toLowerCase(),
            when.toLowerCase()
        );
    }

    /**
     * S3 키 생성
     */
    private String buildS3Key(CardDesignRequest request) {
        Long flowerId = request.mainFlower().getFlowerId();
        String who = normalize(request.whoType());

        // whenTypes를 정렬하여 일관된 키 생성
        String when = (request.whenTypes() == null || request.whenTypes().isEmpty())
                ? "any"
                : request.whenTypes().stream()
                        .map(Enum::name)
                        .sorted()
                        .map(String::toLowerCase)
                        .collect(Collectors.joining("-"));

        String emotion = normalize(request.emotionType());
        String size = normalize(request.bouquetSize());

        return String.format("cards/generated/%d-%s-%s-%s-%s.png",
                flowerId, who, when, emotion, size);
    }

    // Enum 번역 헬퍼 메서드들
    private String translateWhoType(Object whoType) {
        if (whoType == null) return "someone special";
        String name = whoType.toString();
        return switch (name) {
            case "FRIEND" -> "a friend";
            case "FAMILY" -> "family member";
            case "LOVER" -> "a lover";
            case "COLLEAGUE" -> "a colleague";
            default -> "someone special";
        };
    }

    private String translateWhenTypes(List<WhenType> whenTypes) {
        if (whenTypes == null || whenTypes.isEmpty()) {
            return "a special occasion";
        }
        List<String> translations = whenTypes.stream()
                .map(type -> switch (type.name()) {
                    case "BIRTHDAY" -> "a birthday";
                    case "WEDDING" -> "a wedding";
                    case "GRADUATION" -> "a graduation";
                    case "ANNIVERSARY" -> "an anniversary";
                    case "CONGRATULATION" -> "a congratulation";
                    default -> "a special occasion";
                })
                .distinct()
                .toList();
        return String.join(" or ", translations);
    }

    private String translateEmotionType(Object emotionType) {
        if (emotionType == null) return "warm and heartfelt";
        String name = emotionType.toString();
        return switch (name) {
            case "JOY" -> "joyful and cheerful";
            case "LOVE" -> "loving and romantic";
            case "GRATITUDE" -> "grateful and appreciative";
            case "COMFORT" -> "comforting and soothing";
            case "CELEBRATION" -> "celebratory and festive";
            default -> "warm and heartfelt";
        };
    }

    private String translateBouquetSize(Object bouquetSize) {
        if (bouquetSize == null) return "medium";
        String name = bouquetSize.toString();
        return switch (name) {
            case "SMALL" -> "small and intimate";
            case "MEDIUM" -> "medium and balanced";
            case "LARGE" -> "large and impressive";
            default -> "medium";
        };
    }

    private String normalize(Object value) {
        return value == null ? "any" : value.toString().toLowerCase();
    }


    /**
     * 더미 PNG 이미지 생성 (1x1 투명 픽셀)
     * API Key가 없을 때 사용
     */
    private byte[] createDummyPngBytes() {
        return new byte[]{
                (byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
                0x00, 0x00, 0x00, 0x0D, 0x49, 0x48, 0x44, 0x52,
                0x00, 0x00, 0x00, 0x01, 0x00, 0x00, 0x00, 0x01,
                0x08, 0x06, 0x00, 0x00, 0x00, 0x1F, 0x15, (byte) 0xC4,
                (byte) 0x89, 0x00, 0x00, 0x00, 0x0A, 0x49, 0x44, 0x41, 0x54,
                0x78, (byte) 0x9C, 0x63, 0x00, 0x01, 0x00, 0x00, 0x05,
                0x00, 0x01, 0x0D, 0x0A, 0x2D, (byte) 0xB4, 0x00, 0x00,
                0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte) 0xAE, 0x42,
                0x60, (byte) 0x82
        };
    }
}
