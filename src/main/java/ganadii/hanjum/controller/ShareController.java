package ganadii.hanjum.controller;

import ganadii.hanjum.domain.CardFlowers;
import ganadii.hanjum.domain.Flowers;
import ganadii.hanjum.domain.Shares;
import ganadii.hanjum.domain.enums.EmotionType;
import ganadii.hanjum.domain.enums.FlowerType;
import ganadii.hanjum.domain.enums.FriendRequestStatus;
import ganadii.hanjum.dto.ShareDtos;
import ganadii.hanjum.repository.FriendRequestRepository;
import ganadii.hanjum.repository.SharesRepository;
import ganadii.hanjum.service.ShareService;
import ganadii.hanjum.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "04-공유", description = "카드 공유/아카이브")
public class ShareController {

    private final ShareService shareService;
    private final SharesRepository sharesRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final ganadii.hanjum.repository.CardFlowersRepository cardFlowersRepository;

    @PostMapping("/cards/{cardId}/send/self")
    @Operation(summary = "나에게 보내기", description = "내 카드 아카이브에 저장합니다.")
    public ResponseEntity<ShareDtos.ShareResponse> sendToSelf(@PathVariable Long cardId,
                                                              @RequestHeader(name = "X-User-Id", required = false) String userHeader,
                                                              @RequestHeader(name = "Idempotency-Key", required = false) String idemKey) {
        UUID userId = resolveUserId(userHeader);
        String key = trimToNull(idemKey);
        if (key != null) {
            return sharesRepository.findByIdempotencyKeyAndSender_UserId(key, userId)
                    .filter(share -> share.getFlowerCards().getCardId().equals(cardId)
                            && share.getReceiver().getUserId().equals(userId))
                    .map(existing -> ResponseEntity.ok(toResponse(existing)))
                    .orElseGet(() -> {
                        Shares created = shareService.sendToSelf(userId, cardId, key);
                        return ResponseEntity.ok(toResponse(created));
                    });
        }
        Shares created = shareService.sendToSelf(userId, cardId, null);
        return ResponseEntity.ok(toResponse(created));
    }

    @PostMapping("/cards/{cardId}/send")
    @Operation(summary = "친구에게 보내기", description = "카드를 친구에게 전송하고 아카이브에 기록합니다.")
    public ResponseEntity<ShareDtos.ShareResponse> sendToFriend(@PathVariable Long cardId,
                                                                @Valid @RequestBody ShareDtos.SendShareRequest req,
                                                                @RequestHeader(name = "X-User-Id", required = false) String userHeader,
                                                                @RequestHeader(name = "Idempotency-Key", required = false) String idemKey) {
        UUID senderId = resolveUserId(userHeader);
        UUID receiverId = req.receiverId();
        String key = trimToNull(idemKey);
        if (key != null) {
            return sharesRepository.findByIdempotencyKeyAndSender_UserId(key, senderId)
                    .filter(share -> share.getFlowerCards().getCardId().equals(cardId)
                            && share.getReceiver().getUserId().equals(receiverId))
                    .map(existing -> ResponseEntity.ok(toResponse(existing)))
                    .orElseGet(() -> {
                        Shares created = shareService.sendToFriend(senderId, cardId, receiverId,
                                req.toName(), req.fromName(), req.note(), key);
                        return ResponseEntity.ok(toResponse(created));
                    });
        }
        Shares created = shareService.sendToFriend(senderId, cardId, receiverId,
                req.toName(), req.fromName(), req.note(), null);
        return ResponseEntity.ok(toResponse(created));
    }

    @GetMapping("/archive")
    @Operation(summary = "내 아카이브", description = "받은 카드 아카이브를 페이지로 조회합니다. userId 파라미터로 친구 아카이브도 조회 가능합니다. (TEMPORARY)")
    public ResponseEntity<ShareDtos.ArchiveResponse> archive(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "15") int size,
                                                             @RequestParam(required = false) String userId,
                                                             @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        UUID targetUserId = (userId != null && !userId.isBlank())
                ? UUID.fromString(userId.trim())
                : resolveUserId(userHeader);
        Page<Shares> p = sharesRepository.findByReceiver_UserId(
                targetUserId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sharedAt"))
        );
        List<ShareDtos.ShareResponse> items = p.getContent().stream().map(this::toResponse).toList();
        ShareDtos.ArchiveResponse resp = new ShareDtos.ArchiveResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/archive/meta")
    @Operation(summary = "아카이브 메타", description = "친구 요청 여부와 읽지 않은 카드 개수를 확인합니다.")
    public ResponseEntity<ShareDtos.ArchiveMetaResponse> archiveMeta(@RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        UUID userId = resolveUserId(userHeader);
        boolean exists = friendRequestRepository.existsByReceiver_UserIdAndStatus(userId, FriendRequestStatus.PENDING);
        long unreadCount = sharesRepository.countUnreadFromFriends(userId);
        return ResponseEntity.ok(new ShareDtos.ArchiveMetaResponse(exists, (int) unreadCount));
    }

    private UUID resolveUserId(String userHeader) {
        if (userHeader != null && !userHeader.isBlank()) {
            return UUID.fromString(userHeader.trim());
        }
        return SecurityUtils.currentUserIdOrThrow();
    }

    private ShareDtos.ShareResponse toResponse(Shares s) {
        Long cardId = s.getFlowerCards().getCardId();

        Flowers mainFlower = loadMainFlower(cardId);
        Flowers subFlower = loadSubFlower(cardId);

        List<String> emotionTypeNames = buildEmotionTypeNames(s.getFlowerCards().getEmotionTypes());
        List<String> emotionTypeLabels = buildEmotionTypeLabels(s.getFlowerCards().getEmotionTypes());

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
        List<String> backgroundColors = (s.getFlowerCards().getDesignAsset() != null
                && s.getFlowerCards().getDesignAsset().getBackgroundColors() != null)
                ? s.getFlowerCards().getDesignAsset().getBackgroundColors()
                : generateBackgroundColors(mainFlower, subFlower);

        return new ShareDtos.ShareResponse(
                s.getShareId(),
                cardId,
                s.getToName(),
                s.getFromName(),
                s.getNote(),
                s.getIsRead(),
                s.getSharedAt(),
                new ShareDtos.SimpleUser(s.getSender().getUserId(), s.getSender().getFirstName(), s.getSender().getLastName()),
                new ShareDtos.SimpleUser(s.getReceiver().getUserId(), s.getReceiver().getFirstName(), s.getReceiver().getLastName()),
                new ShareDtos.SimpleCard(
                        cardId,
                        s.getFlowerCards().getTitle(),
                        s.getFlowerCards().getImageUrl(),
                        s.getFlowerCards().getWhoType() == null ? null : s.getFlowerCards().getWhoType().name(),
                        s.getFlowerCards().getWhoType() == null ? null : s.getFlowerCards().getWhoType().getLabel(),
                        s.getFlowerCards().getWhenType() == null ? null : s.getFlowerCards().getWhenType().name(),
                        s.getFlowerCards().getWhenType() == null ? null : s.getFlowerCards().getWhenType().getLabel(),
                        emotionTypeNames,
                        emotionTypeLabels,
                        s.getFlowerCards().getBouquetSize() == null ? null : s.getFlowerCards().getBouquetSize().name(),
                        s.getFlowerCards().getBouquetSize() == null ? null : s.getFlowerCards().getBouquetSize().getLabel(),
                        s.getFlowerCards().getWrappingType() == null ? null : s.getFlowerCards().getWrappingType().name(),
                        s.getFlowerCards().getWrappingType() == null ? null : s.getFlowerCards().getWrappingType().getLabel(),
                        backgroundColors,
                        s.getFlowerCards().getImageSource() == null ? null : s.getFlowerCards().getImageSource().name(),
                        s.getFlowerCards().getFloriography(),
                        s.getFlowerCards().getPrice(),
                        s.getFlowerCards().getDesignAsset() == null ? null : s.getFlowerCards().getDesignAsset().getAssetId(),
                        mainFlowerSummary,
                        subFlowerSummary
                )
        );
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private Flowers loadMainFlower(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.MAIN)
                .map(CardFlowers::getFlowers)
                .findFirst()
                .orElse(null);
    }

    private Flowers loadSubFlower(Long cardId) {
        return cardFlowersRepository.findByFlowerCards_CardId(cardId).stream()
                .filter(cf -> cf.getFlowerType() == FlowerType.SUB)
                .map(CardFlowers::getFlowers)
                .findFirst()
                .orElse(null);
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
