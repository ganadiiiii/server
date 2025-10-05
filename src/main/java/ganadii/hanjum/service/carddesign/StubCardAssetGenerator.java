package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.enums.CardImageSource;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import ganadii.hanjum.util.CryptoUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

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
        Long flowerId = request.mainFlower().getFlowerId();
        String who = request.whoType() == null ? "any" : request.whoType().name().toLowerCase();
        String when = request.whenType() == null ? "any" : request.whenType().name().toLowerCase();

        // Use first emotion for key
        String emotion = (request.emotionTypes() == null || request.emotionTypes().isEmpty())
                ? "any"
                : request.emotionTypes().get(0).name().toLowerCase();

        String size = request.bouquetSize() == null ? "any" : request.bouquetSize().name().toLowerCase();
        return flowerId + "-" + who + "-" + when + "-" + emotion + "-" + size;
    }

}
