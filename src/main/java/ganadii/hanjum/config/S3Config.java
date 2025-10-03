package ganadii.hanjum.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@RequiredArgsConstructor
public class S3Config {

    private final S3Properties s3Properties;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(s3Properties.getRegion()))
                .credentialsProvider(awsCredentialsProvider())
                .build();
    }

    private AwsCredentialsProvider awsCredentialsProvider() {
        // Access Key가 설정되어 있으면 직접 사용
        if (s3Properties.getAccessKeyId() != null && !s3Properties.getAccessKeyId().isBlank()) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                            s3Properties.getAccessKeyId(),
                            s3Properties.getSecretAccessKey()
                    )
            );
        }
        // 없으면 Default Provider (IAM Role, 환경변수, ~/.aws/credentials 순서로 탐색)
        return DefaultCredentialsProvider.create();
    }
}
