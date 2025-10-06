package ganadii.hanjum.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.s3")
public class S3Properties {

    private String bucketName;
    private String region = "ap-northeast-2";

    // AWS Access Key ID (optional, IAM Role 사용 시 불필요)
    private String accessKeyId;

    // AWS Secret Access Key (optional, IAM Role 사용 시 불필요)
    private String secretAccessKey;

    // S3 base URL (CloudFront 또는 커스텀 도메인)
    private String baseUrl;
}
