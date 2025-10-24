package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.S3Service;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * S3에 미리 업로드된 디자이너 프리셋 에셋을 조회
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class S3CardAssetPresetLocator implements CardAssetPresetLocator {

    private final S3Service s3Service;

    @Override
    public Optional<CardAssetDescriptor> findPreset(CardDesignRequest request) {
        // S3 키 생성: {flowerId}-{size}.png
        Long flowerId = request.mainFlower().getFlowerId();
        String size = normalize(request.bouquetSize());

        String s3Key = String.format("cards/preset/%d-%s.png", flowerId, size);

        // S3에 파일 존재 여부 확인
        if (s3Service.exists(s3Key)) {
            log.info("Found preset asset in S3: {}", s3Key);
            String imageUrl = s3Service.getPublicUrl(s3Key);
            return Optional.of(new CardAssetDescriptor(
                    imageUrl,
                    s3Key,
                    CardImageSource.PRESET,
                    null,  // checksum은 선택사항
                    null   // backgroundColors는 preset에서 제공하지 않음
            ));
        }

        log.debug("No preset asset found for: {}", s3Key);
        return Optional.empty();
    }

    private String normalize(Object value) {
        return value == null ? "any" : value.toString();
    }
}
