package ganadii.hanjum.service.carddesign.dto;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.enums.BouquetSize;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.WhenType;
import ganadii.hanjum.domain.enums.WhoType;

import java.util.List;

public record CardDesignRequest(
        Flowers mainFlower,
        WhoType whoType,
        List<WhenType> whenTypes,
        EmotionType emotionType,
        BouquetSize bouquetSize
) {
}
