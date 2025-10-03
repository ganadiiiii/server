package ganadii.hanjum.service;

import ganadii.hanjum.config.S3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Properties s3Properties;

    /**
     * S3에 파일 업로드 (InputStream)
     *
     * @param key         S3 객체 키 (예: "cards/generated/a3f2b8-friend-birthday-joy-small.png")
     * @param inputStream 업로드할 파일 스트림
     * @param contentType Content-Type (예: "image/png")
     * @param contentLength 파일 크기
     * @return 업로드된 파일의 Public URL
     */
    public String upload(String key, InputStream inputStream, String contentType, long contentLength) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .contentLength(contentLength)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));

            log.info("Uploaded to S3: bucket={}, key={}", s3Properties.getBucketName(), key);

            return getPublicUrl(key);
        } catch (S3Exception e) {
            log.error("S3 upload failed: key={}, error={}", key, e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    /**
     * S3에 파일 업로드 (byte[])
     *
     * @param key         S3 객체 키
     * @param bytes       업로드할 파일 바이트 배열
     * @param contentType Content-Type
     * @return 업로드된 파일의 Public URL
     */
    public String upload(String key, byte[] bytes, String contentType) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));

            log.info("Uploaded to S3: bucket={}, key={}", s3Properties.getBucketName(), key);

            return getPublicUrl(key);
        } catch (S3Exception e) {
            log.error("S3 upload failed: key={}, error={}", key, e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    /**
     * S3에서 파일 삭제
     *
     * @param key S3 객체 키
     */
    public void delete(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("Deleted from S3: bucket={}, key={}", s3Properties.getBucketName(), key);
        } catch (S3Exception e) {
            log.error("S3 delete failed: key={}, error={}", key, e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    /**
     * S3 객체가 존재하는지 확인
     *
     * @param key S3 객체 키
     * @return 존재 여부
     */
    public boolean exists(String key) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (S3Exception e) {
            log.error("S3 exists check failed: key={}, error={}", key, e.awsErrorDetails().errorMessage(), e);
            throw new RuntimeException("Failed to check file existence in S3", e);
        }
    }

    /**
     * S3 Public URL 생성
     *
     * @param key S3 객체 키
     * @return Public URL
     */
    public String getPublicUrl(String key) {
        // CloudFront 또는 커스텀 도메인이 설정되어 있으면 사용
        if (s3Properties.getBaseUrl() != null && !s3Properties.getBaseUrl().isBlank()) {
            String baseUrl = s3Properties.getBaseUrl();
            if (baseUrl.endsWith("/")) {
                baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            }
            return baseUrl + "/" + key;
        }

        // 기본 S3 URL
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                s3Properties.getBucketName(),
                s3Properties.getRegion(),
                key);
    }
}
