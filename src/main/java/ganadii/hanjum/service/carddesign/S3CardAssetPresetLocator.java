package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.S3Service;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
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

            // Generate background colors for preset cards
            List<String> backgroundColors = generateBackgroundColors(request);

            return Optional.of(new CardAssetDescriptor(
                    imageUrl,
                    s3Key,
                    CardImageSource.PRESET,
                    null,  // checksum은 선택사항
                    backgroundColors
            ));
        }

        log.debug("No preset asset found for: {}", s3Key);
        return Optional.empty();
    }

    private String normalize(Object value) {
        return value == null ? "any" : value.toString();
    }

    /**
     * Generate background colors based on main and sub flower combination
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
