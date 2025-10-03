package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import ganadii.hanjum.util.CryptoUtils;
import org.springframework.stereotype.Component;

@Component
public class StubCardAssetGenerator implements CardAssetGenerator {

    private static final String FALLBACK_BASE_URL = "https://cdn.hanjum.local/generated";

    @Override
    public CardAssetDescriptor generate(CardDesignRequest request) {
        String key = buildKey(request);
        String checksum = CryptoUtils.sha1(key);
        String imageUrl = FALLBACK_BASE_URL + "/" + key + ".png";
        String storageKey = "cards/generated/" + key + ".png";
        return new CardAssetDescriptor(imageUrl, storageKey, CardImageSource.GENERATED, checksum);
    }

    private static String buildKey(CardDesignRequest request) {
        // 해시 기반 꽃 조합 키 (예: a3f2b8c4d5e6f7a8)
        String flowerHash = FlowerCombinationHashGenerator.generateHash(request.mainFlowers());
        String who = request.whoType() == null ? "any" : request.whoType().name().toLowerCase();
        String when = request.whenType() == null ? "any" : request.whenType().name().toLowerCase();
        String emotion = request.emotionType() == null ? "any" : request.emotionType().name().toLowerCase();
        String size = request.bouquetSize() == null ? "any" : request.bouquetSize().name().toLowerCase();
        return flowerHash + "-" + who + "-" + when + "-" + emotion + "-" + size;
    }

}
