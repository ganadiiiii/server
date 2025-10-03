package ganadii.hanjum.service.carddesign.dto;

import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.enums.BouquetSize;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.WhenType;
import ganadii.hanjum.domain.enums.WhoType;

import java.util.List;

public record CardDesignRequest(
        List<Flowers> mainFlowers,
        WhoType whoType,
        WhenType whenType,
        EmotionType emotionType,
        BouquetSize bouquetSize
) {
}
