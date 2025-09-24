package ganadii.hanjum.controller;

import ganadii.hanjum.domain.Shares;
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
    @Operation(summary = "내 아카이브", description = "받은 카드 아카이브를 페이지로 조회합니다.")
    public ResponseEntity<ShareDtos.ArchiveResponse> archive(@RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "15") int size,
                                                             @RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        UUID userId = resolveUserId(userHeader);
        Page<Shares> p = sharesRepository.findByReceiver_UserId(
                userId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sharedAt"))
        );
        List<ShareDtos.ShareResponse> items = p.getContent().stream().map(this::toResponse).toList();
        ShareDtos.ArchiveResponse resp = new ShareDtos.ArchiveResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/archive/meta")
    @Operation(summary = "아카이브 메타", description = "친구 요청이 있는지 여부를 확인합니다.")
    public ResponseEntity<ShareDtos.ArchiveMetaResponse> archiveMeta(@RequestHeader(name = "X-User-Id", required = false) String userHeader) {
        UUID userId = resolveUserId(userHeader);
        boolean exists = friendRequestRepository.existsByReceiver_UserIdAndStatus(userId, FriendRequestStatus.PENDING);
        return ResponseEntity.ok(new ShareDtos.ArchiveMetaResponse(exists));
    }

    private UUID resolveUserId(String userHeader) {
        if (userHeader != null && !userHeader.isBlank()) {
            return UUID.fromString(userHeader.trim());
        }
        return SecurityUtils.currentUserIdOrThrow();
    }

    private ShareDtos.ShareResponse toResponse(Shares s) {
        return new ShareDtos.ShareResponse(
                s.getShareId(),
                s.getFlowerCards().getCardId(),
                s.getToName(),
                s.getFromName(),
                s.getNote(),
                s.getIsRead(),
                s.getSharedAt(),
                new ShareDtos.SimpleUser(s.getSender().getUserId(), s.getSender().getFirstName(), s.getSender().getLastName()),
                new ShareDtos.SimpleUser(s.getReceiver().getUserId(), s.getReceiver().getFirstName(), s.getReceiver().getLastName()),
                new ShareDtos.SimpleCard(
                        s.getFlowerCards().getCardId(),
                        s.getFlowerCards().getTitle(),
                        s.getFlowerCards().getImageUrl(),
                        s.getFlowerCards().getWhoType() == null ? null : s.getFlowerCards().getWhoType().name(),
                        s.getFlowerCards().getWhoType() == null ? null : s.getFlowerCards().getWhoType().getLabel(),
                        s.getFlowerCards().getWhenType() == null ? null : s.getFlowerCards().getWhenType().name(),
                        s.getFlowerCards().getWhenType() == null ? null : s.getFlowerCards().getWhenType().getLabel(),
                        s.getFlowerCards().getEmotionType() == null ? null : s.getFlowerCards().getEmotionType().name(),
                        s.getFlowerCards().getEmotionType() == null ? null : s.getFlowerCards().getEmotionType().getLabel(),
                        s.getFlowerCards().getBouquetSize() == null ? null : s.getFlowerCards().getBouquetSize().name(),
                        s.getFlowerCards().getBouquetSize() == null ? null : s.getFlowerCards().getBouquetSize().getLabel()
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
}
