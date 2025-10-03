package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.S3Service;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
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
    private final RestTemplate restTemplate = new RestTemplate();

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
            String checksum = sha256(imageBytes);

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
            ResponseEntity<Map> response = restTemplate.postForEntity(
                geminiApiUrl,
                entity,
                Map.class
            );

            // 5. Response 파싱
            Map<String, Object> body = response.getBody();
            if (body == null) {
                throw new RuntimeException("Empty response from Gemini API");
            }

            // 응답 구조 로깅
            log.info("Gemini API Response: {}", body);

            // candidates[0].content.parts[0].inlineData.data (Base64)
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates in Gemini response");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) {
                throw new RuntimeException("No content in Gemini response");
            }

            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            if (parts == null || parts.isEmpty()) {
                throw new RuntimeException("No parts in Gemini response");
            }

            // 이미지가 포함된 part 찾기
            Map<String, Object> imagePart = null;
            for (Map<String, Object> part : parts) {
                // inline_data 또는 inlineData 둘 다 체크
                if (part.containsKey("inline_data") || part.containsKey("inlineData")) {
                    imagePart = part;
                    break;
                }
            }

            if (imagePart == null) {
                log.error("No image part found. Parts: {}", parts);
                throw new RuntimeException("No image data in Gemini response. Available parts: " + parts);
            }

            // inline_data 또는 inlineData 가져오기
            Map<String, Object> inlineData = (Map<String, Object>) imagePart.get("inline_data");
            if (inlineData == null) {
                inlineData = (Map<String, Object>) imagePart.get("inlineData");
            }

            if (inlineData == null) {
                throw new RuntimeException("No inline_data in image part. Available keys: " + imagePart.keySet());
            }

            String base64Image = (String) inlineData.get("data");
            if (base64Image == null || base64Image.isEmpty()) {
                throw new RuntimeException("No image data in inline_data");
            }

            // 6. Base64 디코딩
            return Base64.getDecoder().decode(base64Image);

        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    /**
     * 프롬프트 생성
     */
    private String buildPrompt(CardDesignRequest request) {
        // 꽃 정보 수집
        List<String> flowerNames = request.mainFlowers().stream()
            .map(f -> f.getEnglishName() != null ? f.getEnglishName() : f.getKoreanName())
            .collect(Collectors.toList());

        String flowersText = String.join(", ", flowerNames);

        // 시나리오 정보
        String who = translateWhoType(request.whoType());
        String when = translateWhenType(request.whenType());
        String emotion = translateEmotionType(request.emotionType());
        String size = translateBouquetSize(request.bouquetSize());

        // 프롬프트 생성
        return String.format(
            "Create a beautiful flower bouquet image with the following specifications:\n" +
            "- Flowers: %s\n" +
            "- Recipient: %s\n" +
            "- Occasion: %s\n" +
            "- Emotion/Mood: %s\n" +
            "- Size: %s\n" +
            "\n" +
            "Style requirements:\n" +
            "- High quality, photorealistic style\n" +
            "- Clean white or soft gradient background\n" +
            "- Professional florist arrangement\n" +
            "- Centered composition\n" +
            "- Natural lighting\n" +
            "- Fresh and vibrant colors\n" +
            "\n" +
            "The bouquet should convey %s feelings and be perfect for giving to %s on %s.",
            flowersText,
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
        String flowerHash = FlowerCombinationHashGenerator.generateHash(request.mainFlowers());
        String who = normalize(request.whoType());
        String when = normalize(request.whenType());
        String emotion = normalize(request.emotionType());
        String size = normalize(request.bouquetSize());

        return String.format("cards/generated/%s-%s-%s-%s-%s.png",
                flowerHash, who, when, emotion, size);
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

    private String translateWhenType(Object whenType) {
        if (whenType == null) return "a special occasion";
        String name = whenType.toString();
        return switch (name) {
            case "BIRTHDAY" -> "a birthday";
            case "WEDDING" -> "a wedding";
            case "GRADUATION" -> "a graduation";
            case "ANNIVERSARY" -> "an anniversary";
            case "CONGRATULATION" -> "a congratulation";
            default -> "a special occasion";
        };
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

    private String sha256(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
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
