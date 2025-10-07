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
import ganadii.hanjum.domain.enums.WrappingType;
import ganadii.hanjum.dto.FlowerCardDtos;
import ganadii.hanjum.repository.CardFlowersRepository;
import ganadii.hanjum.repository.FlowerCardsRepository;
import ganadii.hanjum.repository.FlowersRepository;
import ganadii.hanjum.repository.SharesRepository;
import ganadii.hanjum.repository.UserRepository;
import ganadii.hanjum.service.carddesign.CardDesignAssetService;
import ganadii.hanjum.service.carddesign.dto.CardDesignRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlowerCardService {

    private final FlowerCardsRepository flowerCardsRepository;
    private final CardFlowersRepository cardFlowersRepository;
    private final FlowersRepository flowersRepository;
    private final UserRepository userRepository;
    private final CardDesignAssetService cardDesignAssetService;
    private final SharesRepository sharesRepository;

    @Transactional
    public FlowerCardDtos.CardResponse createCard(UUID userId, FlowerCardDtos.CreateCardRequest request) {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Long mainFlowerId = request.mainFlowerId();
        if (mainFlowerId == null) {
            throw new IllegalArgumentException("Main flower must be provided");
        }

        Flowers mainFlower = flowersRepository.findById(mainFlowerId)
                .orElseThrow(() -> new IllegalArgumentException("Flower not found"));

        if (request.price() != null && request.price() < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }

        WhoType whoType = resolveEnum(request.whoType(), WhoType.class, WhoType::fromLabel, "whoType");
        WhenType whenType = resolveEnum(request.whenType(), WhenType.class, WhenType::fromLabel, "whenType");
        List<EmotionType> emotionTypes = request.emotionTypes().stream()
                .map(emotionTypeStr -> resolveEnum(emotionTypeStr, EmotionType.class, EmotionType::fromLabel, "emotionType"))
                .distinct()
                .collect(Collectors.toList());
        BouquetSize bouquetSize = resolveBouquetSize(request.bouquetSize());
        WrappingType wrappingType = resolveWrappingType(request.wrappingType());

        // Sub 꽃 선택 (장미, 튤립, 백합 중 랜덤 1개)
        List<Flowers> candidateSubFlowers = flowersRepository.findByKoreanNameIn(List.of("장미", "튤립", "백합"));

        // main이 장미/튤립/백합 중 하나인지 확인
        List<Flowers> subCandidates = candidateSubFlowers.stream()
                .filter(flower -> !flower.getFlowerId().equals(mainFlower.getFlowerId()))
                .toList();

        // 후보가 없으면 전체 중에서 선택 (main과 다른 것만)
        if (subCandidates.isEmpty()) {
            subCandidates = candidateSubFlowers;
        }

        // 랜덤으로 1개 선택
        Flowers selectedSub = null;
        if (!subCandidates.isEmpty()) {
            selectedSub = subCandidates.get(new java.util.Random().nextInt(subCandidates.size()));
        }

        CardDesignAsset designAsset = cardDesignAssetService.resolveAsset(
                new CardDesignRequest(mainFlower, selectedSub, whoType, whenType, emotionTypes, bouquetSize, wrappingType)
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
                .emotionTypes(emotionTypes)
                .bouquetSize(bouquetSize)
                .wrappingType(wrappingType)
                .price(request.price())
                .build();

        FlowerCards saved = flowerCardsRepository.save(card);

        // Main 꽃 저장
        CardFlowers mainLink = CardFlowers.builder()
                .id(new CardFlowersId(saved.getCardId(), mainFlower.getFlowerId()))
                .flowerCards(saved)
                .flowers(mainFlower)
                .flowerType(FlowerType.MAIN)
                .build();
        cardFlowersRepository.save(mainLink);

        // Sub 꽃 저장
        if (selectedSub != null) {
            CardFlowers subLink = CardFlowers.builder()
                    .id(new CardFlowersId(saved.getCardId(), selectedSub.getFlowerId()))
                    .flowerCards(saved)
                    .flowers(selectedSub)
                    .flowerType(FlowerType.SUB)
                    .build();
            cardFlowersRepository.save(subLink);
        }

        return toResponse(saved, mainFlower, selectedSub, saved.getDesignAsset());
    }

    @Transactional(readOnly = true)
    public FlowerCardDtos.CardPageResponse getMyCards(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(normalizePage(page), normalizeSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FlowerCards> cardPage = flowerCardsRepository.findByCreator_UserId(userId, pageable);
        List<FlowerCards> cards = cardPage.getContent();
        Map<Long, Flowers> mainFlowers = loadMainFlowers(cards);
        Map<Long, Flowers> subFlowers = loadSubFlowers(cards);
        List<FlowerCardDtos.CardResponse> responses = cards.stream()
                .map(card -> toResponse(card, mainFlowers.get(card.getCardId()), subFlowers.get(card.getCardId()), card.getDesignAsset()))
                .toList();
        return new FlowerCardDtos.CardPageResponse(
                responses,
                cardPage.getNumber(),
                cardPage.getSize(),
                cardPage.getTotalElements(),
                cardPage.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public FlowerCardDtos.CardResponse getMyCard(UUID userId, Long cardId) {
        FlowerCards card = flowerCardsRepository.findByCardIdAndCreator_UserId(cardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        Flowers mainFlower = resolveMainFlower(cardId);
        Flowers subFlower = resolveSubFlower(cardId);
        return toResponse(card, mainFlower, subFlower, card.getDesignAsset());
    }

    @Transactional
    public void deleteMyCard(UUID userId, Long cardId) {
        FlowerCards card = flowerCardsRepository.findByCardIdAndCreator_UserId(cardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        if (sharesRepository.existsByFlowerCards_CardId(cardId)) {
            throw new IllegalStateException("Shared cards cannot be deleted");
        }
        cardFlowersRepository.deleteByFlowerCards_CardId(cardId);
        flowerCardsRepository.delete(card);
    }

    private static FlowerCardDtos.CardResponse toResponse(FlowerCards card, Flowers mainFlower, Flowers subFlower, CardDesignAsset asset) {
        WhoType whoType = card.getWhoType();
        WhenType whenType = card.getWhenType();
        List<EmotionType> emotionTypes = card.getEmotionTypes();
        BouquetSize bouquetSize = card.getBouquetSize();
        WrappingType wrappingType = card.getWrappingType();

        FlowerCardDtos.FlowerSummary mainFlowerSummary = mainFlower == null
                ? null
                : new FlowerCardDtos.FlowerSummary(
                        mainFlower.getFlowerId(),
                        mainFlower.getKoreanName(),
                        mainFlower.getEnglishName(),
                        mainFlower.getImageUrl()
                );

        FlowerCardDtos.FlowerSummary subFlowerSummary = subFlower == null
                ? null
                : new FlowerCardDtos.FlowerSummary(
                        subFlower.getFlowerId(),
                        subFlower.getKoreanName(),
                        subFlower.getEnglishName(),
                        subFlower.getImageUrl()
                );

        List<String> emotionTypeNames = (emotionTypes == null || emotionTypes.isEmpty())
                ? null
                : emotionTypes.stream().map(EmotionType::name).toList();
        List<String> emotionTypeLabels = (emotionTypes == null || emotionTypes.isEmpty())
                ? null
                : emotionTypes.stream().map(EmotionType::getLabel).toList();

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
                emotionTypeNames,
                emotionTypeLabels,
                bouquetSize == null ? null : bouquetSize.name(),
                bouquetSize == null ? null : bouquetSize.getLabel(),
                wrappingType == null ? null : wrappingType.name(),
                wrappingType == null ? null : wrappingType.getLabel(),
                card.getPrice(),
                asset == null ? null : asset.getAssetId(),
                mainFlowerSummary,
                subFlowerSummary
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
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

    private static WrappingType resolveWrappingType(String raw) {
        if (raw == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        try {
            return WrappingType.valueOf(trimmed.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            try {
                return WrappingType.fromLabel(trimmed);
            } catch (IllegalArgumentException ignored) {
                throw new IllegalArgumentException("Unknown wrappingType: " + raw);
            }
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

    private Flowers resolveMainFlower(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.MAIN)
                .map(CardFlowers::getFlowers)
                .findFirst()
                .orElse(null);
    }

    private Flowers resolveSubFlower(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.SUB)
                .map(CardFlowers::getFlowers)
                .findFirst()
                .orElse(null);
    }

    private Map<Long, Flowers> loadMainFlowers(List<FlowerCards> cards) {
        if (cards == null || cards.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> cardIds = cards.stream()
                .map(FlowerCards::getCardId)
                .filter(Objects::nonNull)
                .toList();
        if (cardIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return cardFlowersRepository.findByFlowerCards_CardIdIn(cardIds).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.MAIN)
                .collect(Collectors.toMap(
                        cf -> cf.getFlowerCards().getCardId(),
                        CardFlowers::getFlowers,
                        (existing, replacement) -> existing  // Keep first if duplicate
                ));
    }

    private Map<Long, Flowers> loadSubFlowers(List<FlowerCards> cards) {
        if (cards == null || cards.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> cardIds = cards.stream()
                .map(FlowerCards::getCardId)
                .filter(Objects::nonNull)
                .toList();
        if (cardIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return cardFlowersRepository.findByFlowerCards_CardIdIn(cardIds).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.SUB)
                .collect(Collectors.toMap(
                        cf -> cf.getFlowerCards().getCardId(),
                        CardFlowers::getFlowers,
                        (existing, replacement) -> existing
                ));
    }

    private static int normalizePage(int page) {
        return page < 0 ? 0 : page;
    }

    private static int normalizeSize(int size) {
        int fallback = 20;
        if (size <= 0) {
            return fallback;
        }
        return Math.min(size, 50);
    }
}
