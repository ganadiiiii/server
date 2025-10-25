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
import ganadii.hanjum.dto.ShareDtos;
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
                .floriography(mainFlower.getDefaultFloriography())
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

    @Transactional
    public ShareDtos.ShareResponse getMyCard(UUID userId, Long cardId) {
        // First try to find card I created
        var createdCard = flowerCardsRepository.findByCardIdAndCreator_UserId(cardId, userId);
        if (createdCard.isPresent()) {
            FlowerCards card = createdCard.get();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            Flowers mainFlower = loadMainFlowerByCardId(cardId);
            Flowers subFlower = loadSubFlowerByCardId(cardId);
            return toShareResponse(card, user, mainFlower, subFlower, card.getDesignAsset());
        }

        // If not found, check if it's a card I received
        var receivedShare = sharesRepository.findByFlowerCards_CardIdAndReceiver_UserId(cardId, userId);
        if (receivedShare.isPresent()) {
            var share = receivedShare.get();
            // Mark as read
            if (share.getIsRead() == null || !share.getIsRead()) {
                sharesRepository.markAsRead(share.getShareId());
            }
            // Load card and flower details
            FlowerCards card = share.getFlowerCards();
            Flowers mainFlower = loadMainFlowerByCardId(cardId);
            Flowers subFlower = loadSubFlowerByCardId(cardId);
            return toReceivedShareResponse(share, card, mainFlower, subFlower, card.getDesignAsset());
        }

        // If not found, check if it's a card my friend received
        var friendReceivedShare = sharesRepository.findByCardIdAndFriendReceiver(cardId, userId);
        if (friendReceivedShare.isPresent()) {
            var share = friendReceivedShare.get();
            // Don't mark as read for friend's cards
            FlowerCards card = share.getFlowerCards();
            Flowers mainFlower = loadMainFlowerByCardId(cardId);
            Flowers subFlower = loadSubFlowerByCardId(cardId);
            return toReceivedShareResponse(share, card, mainFlower, subFlower, card.getDesignAsset());
        }

        throw new IllegalArgumentException("Card not found");
    }

    @Transactional
    public void deleteMyCard(UUID userId, Long cardId) {
        FlowerCards card = flowerCardsRepository.findByCardIdAndCreator_UserId(cardId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        sharesRepository.deleteByFlowerCards_CardId(cardId);
        cardFlowersRepository.deleteByFlowerCards_CardId(cardId);
        flowerCardsRepository.delete(card);
    }

    private static ShareDtos.ShareResponse toShareResponse(FlowerCards card, User user, Flowers mainFlower, Flowers subFlower, CardDesignAsset asset) {
        List<String> emotionTypeNames = buildEmotionTypeNames(card.getEmotionTypes());
        List<String> emotionTypeLabels = buildEmotionTypeLabels(card.getEmotionTypes());

        ShareDtos.SimpleUser simpleUser = new ShareDtos.SimpleUser(
                user.getUserId(),
                user.getFirstName(),
                user.getLastName()
        );

        ShareDtos.SimpleCard simpleCard = buildDetailedCard(card, emotionTypeNames, emotionTypeLabels, mainFlower, subFlower, asset);

        return new ShareDtos.ShareResponse(
                null,  // shareId - not shared yet
                card.getCardId(),
                null,  // toName
                null,  // fromName
                null,  // note
                null,  // isRead
                null,  // sharedAt
                simpleUser,  // sender
                simpleUser,  // receiver - same as sender
                simpleCard
        );
    }

    private static ShareDtos.ShareResponse toReceivedShareResponse(ganadii.hanjum.domain.Shares share, FlowerCards card, Flowers mainFlower, Flowers subFlower, CardDesignAsset asset) {
        List<String> emotionTypeNames = buildEmotionTypeNames(card.getEmotionTypes());
        List<String> emotionTypeLabels = buildEmotionTypeLabels(card.getEmotionTypes());

        ShareDtos.SimpleUser sender = new ShareDtos.SimpleUser(
                share.getSender().getUserId(),
                share.getSender().getFirstName(),
                share.getSender().getLastName()
        );

        ShareDtos.SimpleUser receiver = new ShareDtos.SimpleUser(
                share.getReceiver().getUserId(),
                share.getReceiver().getFirstName(),
                share.getReceiver().getLastName()
        );

        ShareDtos.SimpleCard simpleCard = buildDetailedCard(card, emotionTypeNames, emotionTypeLabels, mainFlower, subFlower, asset);

        return new ShareDtos.ShareResponse(
                share.getShareId(),
                card.getCardId(),
                share.getToName(),
                share.getFromName(),
                share.getNote(),
                true,  // isRead - we just marked it as read
                share.getSharedAt(),
                sender,
                receiver,
                simpleCard
        );
    }

    private static FlowerCardDtos.CardResponse toResponse(FlowerCards card, Flowers mainFlower, Flowers subFlower, CardDesignAsset asset) {
        WhoType whoType = card.getWhoType();
        WhenType whenType = card.getWhenType();
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

        List<String> emotionTypeNames = buildEmotionTypeNames(card.getEmotionTypes());
        List<String> emotionTypeLabels = buildEmotionTypeLabels(card.getEmotionTypes());

        // Get background colors from designAsset, or generate dynamically if null
        List<String> backgroundColors = (asset != null && asset.getBackgroundColors() != null)
                ? asset.getBackgroundColors()
                : generateBackgroundColors(mainFlower, subFlower);

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
                backgroundColors,
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
                    // fall through to throw a unified error below
                }
            }
            throw new IllegalArgumentException("Unknown " + fieldName + ": " + raw);
        }
    }

    private Map<Long, Flowers> loadMainFlowers(List<FlowerCards> cards) {
        List<Long> cardIds = extractCardIds(cards);
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
        List<Long> cardIds = extractCardIds(cards);
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

    private static List<Long> extractCardIds(List<FlowerCards> cards) {
        if (cards == null || cards.isEmpty()) {
            return Collections.emptyList();
        }
        return cards.stream()
                .map(FlowerCards::getCardId)
                .filter(Objects::nonNull)
                .toList();
    }

    private static int normalizePage(int page) {
        return Math.max(page, 0);
    }

    private static int normalizeSize(int size) {
        int fallback = 20;
        if (size <= 0) {
            return fallback;
        }
        return Math.min(size, 50);
    }

    private static List<String> buildEmotionTypeNames(List<EmotionType> emotionTypes) {
        return (emotionTypes == null || emotionTypes.isEmpty())
                ? null
                : emotionTypes.stream().map(EmotionType::name).toList();
    }

    private static List<String> buildEmotionTypeLabels(List<EmotionType> emotionTypes) {
        return (emotionTypes == null || emotionTypes.isEmpty())
                ? null
                : emotionTypes.stream().map(EmotionType::getLabel).toList();
    }

    private static ShareDtos.SimpleCard buildDetailedCard(FlowerCards card, List<String> emotionTypeNames, List<String> emotionTypeLabels, Flowers mainFlower, Flowers subFlower, CardDesignAsset asset) {
        WhoType whoType = card.getWhoType();
        WhenType whenType = card.getWhenType();
        BouquetSize bouquetSize = card.getBouquetSize();
        WrappingType wrappingType = card.getWrappingType();

        ShareDtos.FlowerSummary mainFlowerSummary = mainFlower == null ? null
                : new ShareDtos.FlowerSummary(
                        mainFlower.getFlowerId(),
                        mainFlower.getKoreanName(),
                        mainFlower.getEnglishName(),
                        mainFlower.getImageUrl()
                );

        ShareDtos.FlowerSummary subFlowerSummary = subFlower == null ? null
                : new ShareDtos.FlowerSummary(
                        subFlower.getFlowerId(),
                        subFlower.getKoreanName(),
                        subFlower.getEnglishName(),
                        subFlower.getImageUrl()
                );

        // Get background colors from designAsset, or generate dynamically if null
        List<String> backgroundColors = (asset != null && asset.getBackgroundColors() != null)
                ? asset.getBackgroundColors()
                : generateBackgroundColors(mainFlower, subFlower);

        return new ShareDtos.SimpleCard(
                card.getCardId(),
                card.getTitle(),
                card.getImageUrl(),
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
                backgroundColors,
                card.getImageSource() == null ? null : card.getImageSource().name(),
                card.getFloriography(),
                card.getPrice(),
                asset == null ? null : asset.getAssetId(),
                mainFlowerSummary,
                subFlowerSummary
        );
    }

    private Flowers loadMainFlowerByCardId(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.MAIN)
                .map(CardFlowers::getFlowers)
                .findFirst()
                .orElse(null);
    }

    private Flowers loadSubFlowerByCardId(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.SUB)
                .map(CardFlowers::getFlowers)
                .findFirst()
                .orElse(null);
    }

    /**
     * Generate background colors dynamically based on main and sub flower
     */
    private static List<String> generateBackgroundColors(Flowers mainFlower, Flowers subFlower) {
        if (mainFlower == null) {
            return List.of("#FFFFFF", "#F5F5F5"); // Default white gradient
        }

        List<String> mainColors = getFlowerColors(mainFlower.getFlowerId());

        if (subFlower != null) {
            // Blend colors from both flowers
            List<String> subColors = getFlowerColors(subFlower.getFlowerId());
            return List.of(mainColors.get(0), subColors.get(0));
        }

        // Use main flower's predefined color pair
        return mainColors;
    }

    /**
     * Get predefined color pair for a flower by ID
     */
    private static List<String> getFlowerColors(Long flowerId) {
        return switch (flowerId.intValue()) {
            case 1 -> List.of("#FFAFBC", "#FFDDEA"); // 장미
            case 2 -> List.of("#FFDDD3", "#FED8DA"); // 튤립
            case 3 -> List.of("#FFAB9F", "#FFE0CE"); // 카네이션
            case 4 -> List.of("#F8B36F", "#F7DE81"); // 해바라기
            case 5 -> List.of("#F8D3AF", "#FFFDEE"); // 백합
            case 6 -> List.of("#FFBDAC", "#FFF0CF"); // 거베라
            case 7 -> List.of("#C2D4F3", "#D7EFF3"); // 안개꽃
            case 8 -> List.of("#FFEE8A", "#FFF5D2"); // 프리지아
            case 9 -> List.of("#D8C9E4", "#B9CEDB"); // 은방울꽃
            default -> List.of("#FFFFFF", "#F5F5F5"); // 기본 - 화이트 그라데이션
        };
    }
}
