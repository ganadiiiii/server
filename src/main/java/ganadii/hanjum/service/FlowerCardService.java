package ganadii.hanjum.service;

import ganadii.hanjum.domain.CardDesignAsset;
import ganadii.hanjum.domain.CardFlowers;
import ganadii.hanjum.domain.CardFlowersId;
import ganadii.hanjum.domain.FlowerCards;
import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.User;
import ganadii.hanjum.domain.enums.BouquetSize;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.FlowerType;
import ganadii.hanjum.domain.enums.WhenType;
import ganadii.hanjum.domain.enums.WhoType;
import ganadii.hanjum.dto.FlowerCardDtos;
import ganadii.hanjum.repository.CardFlowersRepository;
import ganadii.hanjum.repository.FlowerCardsRepository;
import ganadii.hanjum.repository.FlowersRepository;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.service.asset.CardDesignAssetService;
import ganadii.hanjum.service.asset.model.CardDesignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FlowerCardService {

    private final FlowerCardsRepository flowerCardsRepository;
    private final CardFlowersRepository cardFlowersRepository;
    private final FlowersRepository flowersRepository;
    private final UserRepository userRepository;
    private final CardDesignAssetService cardDesignAssetService;

    @Transactional
    public FlowerCardDtos.CardResponse createCard(UUID userId, FlowerCardDtos.CreateCardRequest request) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Flowers mainFlower = flowersRepository.findById(request.mainFlowerId())
                .orElseThrow(() -> new IllegalArgumentException("Flower not found"));

        if (request.price() != null && request.price() < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }

        WhoType whoType = resolveEnum(request.whoType(), WhoType.class, WhoType::fromLabel, "whoType");
        WhenType whenType = resolveEnum(request.whenType(), WhenType.class, WhenType::fromLabel, "whenType");
        EmotionType emotionType = resolveEnum(request.emotionType(), EmotionType.class, EmotionType::fromLabel, "emotionType");
        BouquetSize bouquetSize = resolveBouquetSize(request.bouquetSize());
        List<String> backgroundColors = normalizeColors(request.backgroundColors());

        CardDesignAsset designAsset = cardDesignAssetService.resolveAsset(
                new CardDesignRequest(mainFlower, whoType, whenType, emotionType, bouquetSize)
        );

        FlowerCards card = FlowerCards.builder()
                .creator(creator)
                .title(trimToNull(request.title()))
                .imageUrl(designAsset.getImageUrl())
                .imageSource(designAsset.getSource())
                .designAsset(designAsset)
                .floriography(trimToNull(request.floriography()))
                .whoType(whoType)
                .whenType(whenType)
                .emotionType(emotionType)
                .bouquetSize(bouquetSize)
                .price(request.price())
                .backgroundColors(backgroundColors)
                .build();

        FlowerCards saved = flowerCardsRepository.save(card);

        CardFlowers mainLink = CardFlowers.builder()
                .id(new CardFlowersId(saved.getCardId(), mainFlower.getFlowerId()))
                .flowerCards(saved)
                .flowers(mainFlower)
                .flowerType(FlowerType.MAIN)
                .build();
        cardFlowersRepository.save(mainLink);

        return toResponse(saved, mainFlower, saved.getDesignAsset());
    }

    private static FlowerCardDtos.CardResponse toResponse(FlowerCards card, Flowers mainFlower, CardDesignAsset asset) {
        List<String> colors = card.getBackgroundColors() == null ? null : List.copyOf(card.getBackgroundColors());
        WhoType whoType = card.getWhoType();
        WhenType whenType = card.getWhenType();
        EmotionType emotionType = card.getEmotionType();
        BouquetSize bouquetSize = card.getBouquetSize();

        FlowerCardDtos.FlowerSummary flowerSummary = mainFlower == null ? null :
                new FlowerCardDtos.FlowerSummary(
                        mainFlower.getFlowerId(),
                        mainFlower.getKoreanName(),
                        mainFlower.getEnglishName(),
                        mainFlower.getImageUrl()
                );

        return new FlowerCardDtos.CardResponse(
                card.getCardId(),
                card.getTitle(),
                card.getImageUrl(),
                card.getImageSource() == null ? null : card.getImageSource().name(),
                card.getFloriography(),
                whoType == null ? null : whoType.name(),
                whoType == null ? null : whoType.getLabel(),
                whenType == null ? null : whenType.name(),
                whenType == null ? null : whenType.getLabel(),
                emotionType == null ? null : emotionType.name(),
                emotionType == null ? null : emotionType.getLabel(),
                bouquetSize == null ? null : bouquetSize.name(),
                bouquetSize == null ? null : bouquetSize.getLabel(),
                card.getPrice(),
                colors,
                asset == null ? null : asset.getAssetId(),
                flowerSummary
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static List<String> normalizeColors(List<String> colors) {
        if (colors == null || colors.isEmpty()) {
            return null;
        }
        return colors.stream()
                .map(FlowerCardService::trimToNull)
                .filter(s -> s != null)
                .distinct()
                .toList();
    }

    private static BouquetSize resolveBouquetSize(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return BouquetSize.valueOf(trimmed.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unknown bouquetSize: " + raw);
        }
    }

    private static <E extends Enum<E>> E resolveEnum(String raw,
                                                     Class<E> enumType,
                                                     Function<String, E> labelResolver,
                                                     String fieldName) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, trimmed.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            if (labelResolver != null) {
                try {
                    return labelResolver.apply(trimmed);
                } catch (IllegalArgumentException ignored) {
                    // fall through to throw unified error below
                }
            }
            throw new IllegalArgumentException("Unknown " + fieldName + ": " + raw);
        }
    }
}
