package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;

public interface CardAssetGenerator {

    CardAssetDescriptor generate(CardDesignRequest request);
}
