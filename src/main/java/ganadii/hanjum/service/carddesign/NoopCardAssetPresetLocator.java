package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile({"!test"})
public class NoopCardAssetPresetLocator implements CardAssetPresetLocator {

    @Override
    public Optional<CardAssetDescriptor> findPreset(CardDesignRequest request) {
        return Optional.empty();
    }
}
