package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.S3Service;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

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
        // 1. S3 키 생성 (해시 기반)
        String flowerHash = FlowerCombinationHashGenerator.generateHash(request.mainFlowers());
        String who = normalize(request.whoType());
        String when = normalize(request.whenType());
        String emotion = normalize(request.emotionType());
        String size = normalize(request.bouquetSize());

        String s3Key = String.format("cards/preset/%s-%s-%s-%s-%s.png",
                flowerHash, who, when, emotion, size);

        // 2. S3에 파일 존재 여부 확인
        if (s3Service.exists(s3Key)) {
            log.info("Found preset asset in S3: {}", s3Key);
            String imageUrl = s3Service.getPublicUrl(s3Key);
            return Optional.of(new CardAssetDescriptor(
                    imageUrl,
                    s3Key,
                    CardImageSource.PRESET,
                    null  // checksum은 선택사항
            ));
        }

        // 3. 대체 네이밍 시도 (꽃 이름 기반)
        String alternativeKey = buildAlternativeKey(request);
        if (s3Service.exists(alternativeKey)) {
            log.info("Found alternative preset asset in S3: {}", alternativeKey);
            String imageUrl = s3Service.getPublicUrl(alternativeKey);
            return Optional.of(new CardAssetDescriptor(
                    imageUrl,
                    alternativeKey,
                    CardImageSource.PRESET,
                    null
            ));
        }

        log.debug("No preset asset found for: {}", s3Key);
        return Optional.empty();
    }

    /**
     * 대체 네이밍 전략: 꽃 영문명 기반
     * 예: cards/preset/rose-tulip-friend-birthday-joy-medium.png
     */
    private String buildAlternativeKey(CardDesignRequest request) {
        String flowerNames = request.mainFlowers().stream()
                .map(f -> f.getEnglishName() != null ?
                    f.getEnglishName().toLowerCase().replace(" ", "-") :
                    String.valueOf(f.getFlowerId()))
                .sorted()
                .collect(Collectors.joining("-"));

        String who = normalize(request.whoType());
        String when = normalize(request.whenType());
        String emotion = normalize(request.emotionType());
        String size = normalize(request.bouquetSize());

        return String.format("cards/preset/%s-%s-%s-%s-%s.png",
                flowerNames, who, when, emotion, size);
    }

    private String normalize(Object value) {
        return value == null ? "any" : value.toString().toLowerCase();
    }
}
