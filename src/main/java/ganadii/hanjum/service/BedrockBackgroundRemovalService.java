package ganadii.hanjum.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelRequest;
import software.amazon.awssdk.services.bedrockruntime.model.InvokeModelResponse;

import java.util.Base64;

/**
 * Amazon Bedrock Titan Image Generator V2 배경 제거 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BedrockBackgroundRemovalService {

    private final ObjectMapper objectMapper;

    @Value("${aws.bedrock.region:ap-northeast-2}")
    private String bedrockRegion;

    @Value("${aws.bedrock.model-id:amazon.titan-image-generator-v2:0}")
    private String modelId;

    /**
     * 이미지 배경 제거
     *
     * @param imageBytes 원본 이미지 바이트 배열 (JPEG 또는 PNG)
     * @return 배경이 제거된 투명 PNG 이미지 바이트 배열
     */
    public byte[] removeBackground(byte[] imageBytes) {
        log.info("Starting background removal with Bedrock Titan V2");

        try (BedrockRuntimeClient client = createBedrockClient()) {
            // 1. 이미지를 Base64로 인코딩
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);

            // 2. Request Body 생성
            String requestBody = String.format("""
                {
                    "taskType": "BACKGROUND_REMOVAL",
                    "backgroundRemovalParams": {
                        "image": "%s"
                    }
                }
                """, base64Image);

            // 3. Bedrock API 호출
            InvokeModelRequest request = InvokeModelRequest.builder()
                    .modelId(modelId)
                    .body(SdkBytes.fromUtf8String(requestBody))
                    .accept("application/json")
                    .contentType("application/json")
                    .build();

            log.info("Invoking Bedrock model: {} in region: {}", modelId, bedrockRegion);
            InvokeModelResponse response = client.invokeModel(request);

            // 4. Response 파싱
            String responseBody = response.body().asUtf8String();
            JsonNode responseJson = objectMapper.readTree(responseBody);

            // 5. 에러 체크
            JsonNode errorNode = responseJson.get("error");
            if (errorNode != null && !errorNode.isNull()) {
                String errorMessage = errorNode.asText();
                throw new RuntimeException("Bedrock background removal failed: " + errorMessage);
            }

            // 6. 이미지 데이터 추출
            JsonNode imagesNode = responseJson.get("images");
            if (imagesNode == null || !imagesNode.isArray() || imagesNode.isEmpty()) {
                throw new RuntimeException("No images in Bedrock response");
            }

            String resultBase64 = imagesNode.get(0).asText();
            log.info("Background removal completed successfully");

            // 7. Base64 디코딩하여 반환
            return Base64.getDecoder().decode(resultBase64);

        } catch (Exception e) {
            log.error("Failed to remove background with Bedrock", e);
            throw new RuntimeException("Failed to remove background with Bedrock", e);
        }
    }

    /**
     * Bedrock Runtime Client 생성
     */
    private BedrockRuntimeClient createBedrockClient() {
        return BedrockRuntimeClient.builder()
                .region(Region.of(bedrockRegion))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
