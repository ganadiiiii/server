package ganadii.hanjum.repository;

import ganadii.hanjum.domain.CardDesignAsset;
import ganadii.hanjum.domain.enums.BouquetSize;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.WhenType;
import ganadii.hanjum.domain.enums.WhoType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardDesignAssetRepository extends JpaRepository<CardDesignAsset, Long> {
    Optional<CardDesignAsset> findByFlowerCombinationHashAndWhoTypeAndWhenTypeAndEmotionTypeAndBouquetSize(
            String flowerCombinationHash,
            WhoType whoType,
            WhenType whenType,
            EmotionType emotionType,
            BouquetSize bouquetSize
    );
}
