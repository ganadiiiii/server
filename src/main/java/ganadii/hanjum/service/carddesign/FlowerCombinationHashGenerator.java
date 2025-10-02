package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.Flowers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 꽃 조합에 대한 해시 생성 유틸리티
 */
public class FlowerCombinationHashGenerator {

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

        return sha256(flowerIds).substring(0, 16);
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm not available", e);
        }
    }
}
