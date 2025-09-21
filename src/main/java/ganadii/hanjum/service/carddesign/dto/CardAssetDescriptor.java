package ganadii.hanjum.service.carddesign.dto;

import ganadii.hanjum.domain.enums.CardImageSource;

public record CardAssetDescriptor(
        String imageUrl,
        String storageKey,
        CardImageSource source,
        String checksum
) {
}
