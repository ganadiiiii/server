package ganadii.hanjum.service.carddesign;

import ganadii.hanjum.domain.CardDesignAsset;
import ganadii.hanjum.domain.enums.BouquetSize;
import ganadii.hanjum.repository.CardDesignAssetRepository;
import ganadii.hanjum.service.carddesign.dto.CardAssetDescriptor;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardDesignAssetService {

    private final CardDesignAssetRepository cardDesignAssetRepository;
    private final List<CardAssetPresetLocator> presetLocators;
    private final CardAssetGenerator cardAssetGenerator;

    @Transactional
    public CardDesignAsset resolveAsset(CardDesignRequest request) {
        Objects.requireNonNull(request, "request must not be null");
        if (request.mainFlower() == null) {
            throw new IllegalArgumentException("Main flower must be provided");
        }
        if (request.emotionTypes() == null || request.emotionTypes().isEmpty()) {
            throw new IllegalArgumentException("Emotion types must be provided");
        }

        String flowerHash = FlowerCombinationHashGenerator.generateHash(request.mainFlower());

        // Use first emotion for cache key
        return cardDesignAssetRepository.findByFlowerCombinationHashAndWhoTypeAndWhenTypeAndEmotionTypeAndBouquetSize(
                flowerHash,
                request.whoType(),
                request.whenType(),
                request.emotionTypes().get(0),
                request.bouquetSize()
        ).orElseGet(() -> fetchAndCache(request, flowerHash));
    }

    private CardDesignAsset fetchAndCache(CardDesignRequest request, String flowerHash) {
        for (CardAssetPresetLocator locator : safeLocators()) {
            Optional<CardAssetDescriptor> preset = locator.findPreset(request);
            if (preset.isPresent()) {
                return persist(request, flowerHash, preset.get());
            }
        }
        CardAssetDescriptor generated = cardAssetGenerator.generate(request);
        return persist(request, flowerHash, generated);
    }

    private CardDesignAsset persist(CardDesignRequest request, String flowerHash, CardAssetDescriptor descriptor) {
        Objects.requireNonNull(descriptor, "descriptor must not be null");
        if (descriptor.imageUrl() == null || descriptor.imageUrl().isBlank()) {
            throw new IllegalArgumentException("Asset descriptor must supply a non-empty imageUrl");
        }
        if (descriptor.source() == null) {
            throw new IllegalArgumentException("Asset descriptor must declare an image source");
        }

        // Use first emotion for cache key
        CardDesignAsset asset = CardDesignAsset.builder()
                .flowerCombinationHash(flowerHash)
                .whoType(request.whoType())
                .whenType(request.whenType())
                .emotionType(request.emotionTypes().get(0))
                .bouquetSize(normalizeBouquetSize(request.bouquetSize()))
                .source(descriptor.source())
                .imageUrl(descriptor.imageUrl())
                .storageKey(descriptor.storageKey())
                .checksum(descriptor.checksum())
                .build();
        try {
            return cardDesignAssetRepository.save(asset);
        } catch (DataIntegrityViolationException e) {
            return cardDesignAssetRepository.findByFlowerCombinationHashAndWhoTypeAndWhenTypeAndEmotionTypeAndBouquetSize(
                    flowerHash,
                    request.whoType(),
                    request.whenType(),
                    request.emotionTypes().get(0),
                    normalizeBouquetSize(request.bouquetSize())
            ).orElseThrow(() -> e);
        }
    }

    private static BouquetSize normalizeBouquetSize(BouquetSize size) {
        return size;
    }

    private List<CardAssetPresetLocator> safeLocators() {
        return presetLocators == null ? Collections.emptyList() : presetLocators;
    }
}
