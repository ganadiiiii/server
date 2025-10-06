package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.util.CryptoUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 꽃 조합에 대한 해시 생성 유틸리티
 */
public class FlowerCombinationHashGenerator {

    private FlowerCombinationHashGenerator() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 꽃 리스트를 해시로 변환
     *
     * @param flowers 꽃 리스트 (순서 무관, 자동 정렬)
     * @return SHA-256 해시의 앞 16자리
     *
     * 예시:
     * [장미(1), 튤립(5), 백합(12)] → "1,5,12" → SHA-256 → "a3f2b8c4d5e6f7a8"
     */
    public static String generateHash(List<Flowers> flowers) {
        if (flowers == null || flowers.isEmpty()) {
            throw new IllegalArgumentException("Flowers list must not be empty");
        }

        // flowerId를 정렬하여 일관성 보장
        String flowerIds = flowers.stream()
                .map(Flowers::getFlowerId)
                .sorted()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return CryptoUtils.sha256(flowerIds).substring(0, 16);
    }

    /**
     * 단일 꽃을 해시로 변환
     *
     * @param flower 꽃
     * @return SHA-256 해시의 앞 16자리
     *
     * 예시:
     * 장미(1) → "1" → SHA-256 → "a3f2b8c4d5e6f7a8"
     */
    public static String generateHash(Flowers flower) {
        if (flower == null) {
            throw new IllegalArgumentException("Flower must not be null");
        }

        String flowerId = String.valueOf(flower.getFlowerId());
        return CryptoUtils.sha256(flowerId).substring(0, 16);
    }
}
