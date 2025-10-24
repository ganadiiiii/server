package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.WhenType;
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
import java.util.*;

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
    private final ganadii.hanjum.service.BedrockBackgroundRemovalService bedrockBackgroundRemovalService;

    // S3 경로 constants
    private static final String FLOWER_ASSETS_PATH = "main_flowers";
    private static final String BOUQUET_REFERENCE_PATH = "cards/reference";

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
            byte[] geminiImageBytes = callGeminiApi(request);
            log.info("Gemini image generated, size: {} bytes", geminiImageBytes.length);

            // 3. Bedrock 배경 제거
            byte[] transparentImageBytes = bedrockBackgroundRemovalService.removeBackground(geminiImageBytes);
            log.info("Background removed, size: {} bytes", transparentImageBytes.length);

            // 4. S3 업로드 (투명 배경 이미지)
            String imageUrl = s3Service.upload(s3Key, transparentImageBytes, "image/png");

            // 5. 체크섬 계산 (최종 투명 이미지 기준)
            String checksum = CryptoUtils.sha256(transparentImageBytes);

            log.info("Card image generated successfully: s3Key={}, url={}", s3Key, imageUrl);

            // 6. Generate background colors based on flower combination
            List<String> backgroundColors = generateBackgroundColors(request);

            return new CardAssetDescriptor(imageUrl, s3Key, CardImageSource.GENERATED, checksum, backgroundColors);

        } catch (Exception e) {
            log.error("Failed to generate card image", e);
            throw new RuntimeException("Failed to generate card image via Gemini API and Bedrock", e);
        }
    }

    /**
     * 개별 꽃 에셋 이미지를 Base64로 로드
     */
    private String loadFlowerAssetAsBase64(Flowers flower) {
        if (flower == null) {
            return null;
        }

        try {
            String fileName = flower.getEnglishName()
                    .toLowerCase()
                    .replace(" ", "")
                    .replace("'", "");

            String assetKey = String.format("%s/%s.png", FLOWER_ASSETS_PATH, fileName);

            if (s3Service.exists(assetKey)) {
                byte[] imageBytes = s3Service.download(assetKey);
                if (imageBytes == null || imageBytes.length == 0) {
                    log.warn("Flower asset is empty: {}", assetKey);
                    return null;
                }
                log.info("Flower asset loaded: {} → {} (size={})", flower.getKoreanName(), assetKey, imageBytes.length);
                return Base64.getEncoder().encodeToString(imageBytes);
            }

            log.warn("Flower asset not found: {}", assetKey);
            return null;
        } catch (Exception e) {
            log.error("Failed to load flower asset: {}", flower.getKoreanName(), e);
            return null;
        }
    }

    /**
     * 꽃다발 스타일 참고 이미지를 Base64로 로드
     * 네이밍 규칙: {flowerId}-{size}.png (예: 1-L.png, 7-M.png)
     */
    private String loadBouquetStyleReferenceAsBase64(CardDesignRequest request) {
        Long flowerId = request.mainFlower().getFlowerId();
        String size = convertBouquetSizeToS3Format(request.bouquetSize());

        String referenceKey = String.format("%s/%d-%s.png", BOUQUET_REFERENCE_PATH, flowerId, size);

        try {
            if (s3Service.exists(referenceKey)) {
                byte[] imageBytes = s3Service.download(referenceKey);
                if (imageBytes == null || imageBytes.length == 0) {
                    log.warn("Bouquet reference is empty: {}", referenceKey);
                    return null;
                }
                log.info("Bouquet style reference loaded: {} (size={})", referenceKey, imageBytes.length);
                return Base64.getEncoder().encodeToString(imageBytes);
            }

            log.warn("Bouquet reference not found: {}", referenceKey);
        } catch (Exception e) {
            log.warn("Failed to load bouquet reference: {}", referenceKey, e);
        }

        // Fallback 1: Try same flower with medium size
        if (!"M".equals(size)) {
            try {
                String fallbackKey = String.format("%s/%d-M.png", BOUQUET_REFERENCE_PATH, flowerId);
                if (s3Service.exists(fallbackKey)) {
                    byte[] imageBytes = s3Service.download(fallbackKey);
                    log.info("Using fallback bouquet reference (medium size): {}", fallbackKey);
                    return Base64.getEncoder().encodeToString(imageBytes);
                }
            } catch (Exception e) {
                log.warn("Fallback to medium size also failed", e);
            }
        }

        // Fallback 2: Try any size for this flower
        try {
            for (String fallbackSize : List.of("M", "S", "L")) {
                String fallbackKey = String.format("%s/%d-%s.png", BOUQUET_REFERENCE_PATH, flowerId, fallbackSize);
                if (s3Service.exists(fallbackKey)) {
                    byte[] imageBytes = s3Service.download(fallbackKey);
                    log.info("Using fallback bouquet reference (any size): {}", fallbackKey);
                    return Base64.getEncoder().encodeToString(imageBytes);
                }
            }
        } catch (Exception e) {
            log.warn("All fallback attempts failed", e);
        }

        return null;
    }

    /**
     * Gemini API 호출 (트리플 이미지 입력 방식)
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

            // 2. 3개 이미지 로드
            String mainFlowerBase64 = loadFlowerAssetAsBase64(request.mainFlower());
            String subFlowerBase64 = loadFlowerAssetAsBase64(request.subFlower());
            String bouquetStyleBase64 = loadBouquetStyleReferenceAsBase64(request);

            // 3. Request Body 구성 (3개 이미지 + 텍스트 프롬프트)
            List<Map<String, Object>> parts = new java.util.ArrayList<>();

            // 이미지 1: 메인 꽃 개별 에셋
            if (mainFlowerBase64 != null) {
                parts.add(Map.of(
                    "inline_data", Map.of(
                        "mime_type", "image/png",
                        "data", mainFlowerBase64
                    )
                ));
                log.info("Main flower asset included: {}", request.mainFlower().getKoreanName());
            }

            // 이미지 2: 서브 꽃 개별 에셋
            if (subFlowerBase64 != null) {
                parts.add(Map.of(
                    "inline_data", Map.of(
                        "mime_type", "image/png",
                        "data", subFlowerBase64
                    )
                ));
                log.info("Sub flower asset included: {}", request.subFlower().getKoreanName());
            }

            // 이미지 3: 꽃다발 스타일 참고
            if (bouquetStyleBase64 != null) {
                parts.add(Map.of(
                    "inline_data", Map.of(
                        "mime_type", "image/png",
                        "data", bouquetStyleBase64
                    )
                ));
                log.info("Bouquet style reference included: {}",
                    request.bouquetSize() == null ? "medium" : request.bouquetSize().name().toLowerCase());
            }

            // 텍스트 프롬프트 추가
            parts.add(Map.of("text", prompt));

            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", parts)
                ),
                "generationConfig", Map.of(
                    "responseModalities", List.of("Image")
                )
            );

            // 4. Headers 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("x-goog-api-key", geminiApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            // 5. API 호출
            log.info("Calling Gemini API with {} images: {}", parts.size() - 1, geminiApiUrl);
            ResponseEntity<GeminiApiResponse.Response> response = restTemplate.postForEntity(
                geminiApiUrl,
                entity,
                GeminiApiResponse.Response.class
            );

            // 6. Response 파싱
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

            // 7. Base64 디코딩
            return Base64.getDecoder().decode(inlineData.data());

        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new RuntimeException("Failed to call Gemini API", e);
        }
    }

    /**
     * 프롬프트 생성 (트리플 이미지 전략) - legacy
    private String buildPrompt(CardDesignRequest request) {
        // 꽃 정보
        Flowers mainFlower = request.mainFlower();
        Flowers subFlower = request.subFlower();
        String mainFlowerName = mainFlower.getEnglishName() != null
                ? mainFlower.getEnglishName()
                : mainFlower.getKoreanName();
        String subFlowerName = subFlower != null && subFlower.getEnglishName() != null
                ? subFlower.getEnglishName()
                : (subFlower != null ? subFlower.getKoreanName() : "complementary flowers");

        // 시나리오 정보
        String who = translateWhoType(request.whoType());
        String when = translateWhenType(request.whenType());
        String emotion = translateEmotionTypes(request.emotionTypes());
        String size = translateBouquetSize(request.bouquetSize());
        String wrapping = translateWrappingType(request.wrappingType());

        // 프롬프트 생성
        return String.format(
            """
            You are provided with THREE reference images:
            1. Image 1: Individual {mainFlowerName} flower asset (transparent background)
            2. Image 2: Individual {subFlowerName} flower asset (transparent background)
            3. Image 3: Complete bouquet style reference showing composition and arrangement

            Create a **beautiful, photorealistic flower bouquet** with these specifications:
            - Main Flower: {mainFlowerName} (must be prominent, about 50% of the bouquet)
            - Supporting Flower: {subFlowerName} (supporting role, about 30% of the bouquet)
            - Additional Flowers: Add 2–3 complementary flowers (make up about 20% of the bouquet) for visual variety
            - Recipient: {who}
            - Occasion: {when}
            - Emotion/Mood: {emotion}
            - Size: {size}
            
            **CRITICAL REQUIREMENTS:**
            1. COMPOSITION: Use Image 1 ({mainFlowerName}) as the centerpiece flower (approx. 50% prominence).
            2. SUPPORTING: Integrate Image 2 ({subFlowerName}) as supporting elements (approx. 30%).
            3. VARIETY: Add 2–3 other complementary flowers (approx. 20%).
            4. STYLE: Match Image 3’s exact visual style —
            - Photography style and lighting
            - Background treatment (color, gradient, texture)
            - Bouquet arrangement and overall mood
            - Image quality and resolution
            
            **BACKGROUND TRANSPARENCY SPECIFICATION:**
            - The background must be fully transparent around the bouquet.
            - Only the bouquet (flowers and stems) appear; there should be **no visible wrapping**, no visible environment or container.
            - The final output should ideally be a high-resolution PNG with an alpha channel (transparent background).
            
            **Additional requirements:**
            - High quality, photorealistic style
            - Professional florist arrangement
            - Centered composition of the bouquet in the image
            - Natural lighting, fresh and vibrant colors
            
            The bouquet should convey {emotion} feelings and be perfect for giving to {who} on {when}.
            """,
            mainFlowerName,
            subFlowerName,
            mainFlowerName,
            subFlowerName,
            who,
            when,
            emotion,
            size,
            wrapping,
            mainFlowerName,
            subFlowerName,
            wrapping.toLowerCase(),
            emotion.toLowerCase(),
            who.toLowerCase(),
            when.toLowerCase()
        );
    }
     */

    // 프롬프트 생성 (트리플 이미지 전략, 포장지 제거 및 투명 배경 지정)
    private String buildPrompt(CardDesignRequest request) {
        Flowers mainFlower = request.mainFlower();
        Flowers subFlower = request.subFlower();

        String mainFlowerName = mainFlower.getEnglishName() != null
                ? mainFlower.getEnglishName()
                : mainFlower.getKoreanName();

        String subFlowerName = subFlower != null && subFlower.getEnglishName() != null
                ? subFlower.getEnglishName()
                : (subFlower != null ? subFlower.getKoreanName() : "complementary flowers");

        String who = translateWhoType(request.whoType());
        String when = translateWhenType(request.whenType());
        String emotion = translateEmotionTypes(request.emotionTypes());
        String size = translateBouquetSize(request.bouquetSize());

        return String.format(
                """
                You are provided with THREE reference images:
                1. Image 1: Individual %s flower asset (transparent background)
                2. Image 2: Individual %s flower asset (transparent background)
                3. Image 3: Complete bouquet style reference showing composition and arrangement

                Create a beautiful, photorealistic flower bouquet with these specifications:
                - Main Flower: %s (must be prominent, about 50%% of the bouquet)
                - Supporting Flower: %s (supporting role, about 30%% of the bouquet)
                - Additional Flowers: Add 2–3 complementary flowers (make up about 20%% of the bouquet) for visual variety
                - Recipient: %s
                - Occasion: %s
                - Emotion/Mood: %s
                - Size: %s

                CRITICAL REQUIREMENTS:
                1. COMPOSITION: Use Image 1 (%s) as the centerpiece flower (approx. 50%% prominence)
                2. SUPPORTING: Integrate Image 2 (%s) as supporting elements (approx. 30%%)
                3. VARIETY: Add 2–3 other complementary flowers (approx. 20%%)
                4. STYLE: Match Image 3's exact visual style:
                   - Photography style and lighting
                   - Background treatment (color, gradient, texture)
                   - Bouquet arrangement and overall mood
                   - Image quality and resolution

                BACKGROUND SPECIFICATION:
                - Create a clean, attractive background suitable for the bouquet
                - Use soft, natural colors or gentle gradients
                - Focus on making the flowers stand out with professional lighting
                - No visible wrapping, vase, ribbon, or other props

                Additional requirements:
                - High quality, photorealistic style
                - Professional florist arrangement
                - Centered composition of the bouquet in the image
                - Natural lighting, fresh and vibrant colors

                The bouquet should convey %s feelings and be perfect for giving to %s on %s.
                """,
                mainFlowerName,  // Image 1
                subFlowerName,   // Image 2
                mainFlowerName,  // Main Flower description
                subFlowerName,   // Supporting Flower description
                who,
                when,
                emotion,
                size,
                mainFlowerName,  // For COMPOSITION
                subFlowerName,   // For SUPPORTING
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
        String when = normalize(request.whenType());

        // Use first emotion for S3 key
        String emotion = (request.emotionTypes() == null || request.emotionTypes().isEmpty())
                ? "any"
                : request.emotionTypes().get(0).name().toLowerCase();

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

    private String translateWhenType(WhenType whenType) {
        if (whenType == null) {
            return "a special occasion";
        }
        return switch (whenType.name()) {
            case "BIRTHDAY" -> "a birthday";
            case "WEDDING" -> "a wedding";
            case "GRADUATION" -> "a graduation";
            case "ANNIVERSARY" -> "an anniversary";
            case "CONGRATULATION" -> "a congratulation";
            default -> "a special occasion";
        };
    }

    private String translateEmotionTypes(List<EmotionType> emotionTypes) {
        if (emotionTypes == null || emotionTypes.isEmpty()) {
            return "warm and heartfelt";
        }
        List<String> translations = emotionTypes.stream()
                .map(type -> switch (type.name()) {
                    case "JOY" -> "joyful and cheerful";
                    case "LOVE" -> "loving and romantic";
                    case "GRATITUDE" -> "grateful and appreciative";
                    case "COMFORT" -> "comforting and soothing";
                    case "CELEBRATION" -> "celebratory and festive";
                    default -> "warm and heartfelt";
                })
                .distinct()
                .toList();
        return String.join(" and ", translations);
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

    private String translateWrappingType(Object wrappingType) {
        if (wrappingType == null) return "kraft paper";
        String name = wrappingType.toString();
        return switch (name) {
            case "KRAFT_PAPER" -> "kraft paper";
            case "COLOR_PAPER" -> "colored paper";
            case "CLEAR_VINYL" -> "clear vinyl";
            default -> "kraft paper";
        };
    }

    private String normalize(Object value) {
        return value == null ? "any" : value.toString().toLowerCase();
    }

    /**
     * BouquetSize enum을 S3 파일명 형식으로 변환
     * SMALL -> "S", MEDIUM -> "M", LARGE -> "L"
     */
    private String convertBouquetSizeToS3Format(Object bouquetSize) {
        if (bouquetSize == null) {
            return "M";  // Default to Medium
        }
        String name = bouquetSize.toString();
        return switch (name) {
            case "SMALL" -> "S";
            case "MEDIUM" -> "M";
            case "LARGE" -> "L";
            default -> "M";
        };
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

    /**
     * Generate background colors based on main and sub flower combination
     *
     * @param request Card design request containing flower information
     * @return List of hex color strings for gradient background
     */
    private List<String> generateBackgroundColors(CardDesignRequest request) {
        Flowers mainFlower = request.mainFlower();
        Flowers subFlower = request.subFlower();

        // Get predefined color pair for main flower
        List<String> mainColors = getFlowerColors(mainFlower.getFlowerId());

        if (subFlower != null) {
            // If sub flower exists, blend colors from both flowers
            List<String> subColors = getFlowerColors(subFlower.getFlowerId());
            return List.of(mainColors.get(0), subColors.get(0));
        }

        // If no sub flower, use main flower's predefined color pair
        return mainColors;
    }

    /**
     * Get predefined color pair for a flower by ID
     * Colors are based on design specifications
     */
    private List<String> getFlowerColors(Long flowerId) {
        return switch (flowerId.intValue()) {
            case 1 -> List.of("#FFAFBC", "#FFDDEA"); // 장미
            case 2 -> List.of("#FFDDD3", "#FED8DA"); // 튤립
            case 3 -> List.of("#FFAB9F", "#FFE0CE"); // 카네이션
            case 4 -> List.of("#F8B36F", "#F7DE81"); // 해바라기
            case 5 -> List.of("#F8D3AF", "#FFFDEE"); // 백합
            case 6 -> List.of("#FFBDAC", "#FFF0CF"); // 거베라
            case 7 -> List.of("#C2D4F3", "#D7EFF3"); // 안개꽃
            case 8 -> List.of("#FFEE8A", "#FFF5D2"); // 프리지아
            case 9 -> List.of("#D8C9E4", "#B9CEDB"); // 은방울꽃
            default -> List.of("#FFFFFF", "#F5F5F5"); // 기본 - 화이트 그라데이션
        };
    }
}
