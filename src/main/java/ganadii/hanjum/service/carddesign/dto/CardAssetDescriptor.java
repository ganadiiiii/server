package ganadii.hanjum.service.carddesign.dto;

import ganadii.hanjum.domain.enums.CardImageSource;

import java.util.List;

public record CardAssetDescriptor(
        String imageUrl,
        String storageKey,
        CardImageSource source,
        String checksum,
        List<String> backgroundColors
) {
}
