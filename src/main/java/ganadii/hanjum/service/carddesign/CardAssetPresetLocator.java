package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;

import java.util.Optional;

public interface CardAssetPresetLocator {

    Optional<CardAssetDescriptor> findPreset(CardDesignRequest request);
}
