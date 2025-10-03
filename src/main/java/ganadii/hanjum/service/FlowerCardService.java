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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

        List<Long> requestedIds = request.mainFlowerIds();
        if (requestedIds == null || requestedIds.isEmpty()) {
            throw new IllegalArgumentException("At least one main flower must be provided");
        }

        List<Long> normalizedIds = requestedIds.stream()
                .map(id -> Objects.requireNonNull(id, "mainFlowerIds must not contain null"))
                .map(Long::valueOf)
                .collect(Collectors.toCollection(ArrayList::new));

        LinkedHashSet<Long> distinctIds = new LinkedHashSet<>(normalizedIds);
        if (distinctIds.size() != normalizedIds.size()) {
            throw new IllegalArgumentException("Duplicate main flower ids are not allowed");
        }

        List<Flowers> fetched = flowersRepository.findAllById(distinctIds);
        Map<Long, Flowers> flowersById = fetched.stream()
                .collect(Collectors.toMap(Flowers::getFlowerId, Function.identity()));
        if (flowersById.size() != distinctIds.size()) {
            throw new IllegalArgumentException("One or more flowers were not found");
        }

        List<Flowers> orderedMainFlowers = distinctIds.stream()
                .map(flowersById::get)
                .collect(Collectors.toCollection(ArrayList::new));

        if (request.price() != null && request.price() < 0) {
            throw new IllegalArgumentException("price must be >= 0");
        }

        WhoType whoType = resolveEnum(request.whoType(), WhoType.class, WhoType::fromLabel, "whoType");
        WhenType whenType = resolveEnum(request.whenType(), WhenType.class, WhenType::fromLabel, "whenType");
        EmotionType emotionType = resolveEnum(request.emotionType(), EmotionType.class, EmotionType::fromLabel, "emotionType");
        BouquetSize bouquetSize = resolveBouquetSize(request.bouquetSize());

        CardDesignAsset designAsset = cardDesignAssetService.resolveAsset(
                new CardDesignRequest(orderedMainFlowers, whoType, whenType, emotionType, bouquetSize)
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
                .build();

        FlowerCards saved = flowerCardsRepository.save(card);

        List<CardFlowers> links = orderedMainFlowers.stream()
                .map(flower -> CardFlowers.builder()
                        .id(new CardFlowersId(saved.getCardId(), flower.getFlowerId()))
                        .flowerCards(saved)
                        .flowers(flower)
                        .flowerType(FlowerType.MAIN)
                        .build())
                .toList();
        cardFlowersRepository.saveAll(links);

        return toResponse(saved, orderedMainFlowers, saved.getDesignAsset());
    }

    @Transactional(readOnly = true)
    public FlowerCardDtos.CardPageResponse getMyCards(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(normalizePage(page), normalizeSize(size), Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<FlowerCards> cardPage = flowerCardsRepository.findByCreator_UserId(userId, pageable);
        List<FlowerCards> cards = cardPage.getContent();
        Map<Long, List<Flowers>> mainFlowers = loadMainFlowers(cards);
        List<FlowerCardDtos.CardResponse> responses = cards.stream()
                .map(card -> toResponse(card, mainFlowers.getOrDefault(card.getCardId(), List.of()), card.getDesignAsset()))
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
        List<Flowers> mainFlowers = resolveMainFlowers(cardId);
        return toResponse(card, mainFlowers, card.getDesignAsset());
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

    private static FlowerCardDtos.CardResponse toResponse(FlowerCards card, List<Flowers> mainFlowers, CardDesignAsset asset) {
        WhoType whoType = card.getWhoType();
        WhenType whenType = card.getWhenType();
        EmotionType emotionType = card.getEmotionType();
        BouquetSize bouquetSize = card.getBouquetSize();
        List<FlowerCardDtos.FlowerSummary> flowerSummaries = (mainFlowers == null || mainFlowers.isEmpty())
                ? List.of()
                : mainFlowers.stream()
                .sorted(Comparator.comparing(Flowers::getFlowerId))
                .map(flower -> new FlowerCardDtos.FlowerSummary(
                        flower.getFlowerId(),
                        flower.getKoreanName(),
                        flower.getEnglishName(),
                        flower.getImageUrl()
                ))
                .toList();

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
                asset == null ? null : asset.getAssetId(),
                flowerSummaries
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

    private List<Flowers> resolveMainFlowers(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.MAIN)
                .map(CardFlowers::getFlowers)
                .sorted(Comparator.comparing(Flowers::getFlowerId))
                .toList();
    }

    private Map<Long, List<Flowers>> loadMainFlowers(List<FlowerCards> cards) {
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
        Map<Long, List<CardFlowers>> grouped = cardFlowersRepository.findByFlowerCards_CardIdIn(cardIds).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.MAIN)
                .collect(Collectors.groupingBy(cf -> cf.getFlowerCards().getCardId()));

        Map<Long, List<Flowers>> result = new HashMap<>();
        for (Map.Entry<Long, List<CardFlowers>> entry : grouped.entrySet()) {
            List<Flowers> ordered = entry.getValue().stream()
                    .map(CardFlowers::getFlowers)
                    .sorted(Comparator.comparing(Flowers::getFlowerId))
                    .toList();
            result.put(entry.getKey(), ordered);
        }
        return result;
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
