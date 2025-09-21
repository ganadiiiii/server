package ganadii.hanjum.controller;

import ganadii.hanjum.domain.Shares;
import ganadii.hanjum.repository.FriendRequestRepository;
import ganadii.hanjum.domain.enums.FriendRequestStatus;
import ganadii.hanjum.repository.SharesRepository;
import ganadii.hanjum.service.ShareService;
import ganadii.hanjum.dto.ShareDtos;
import ganadii.hanjum.web.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final SharesRepository sharesRepository;
    private final FriendRequestRepository friendRequestRepository;

    @PostMapping("/cards/{cardId}/send/self")
    public ResponseEntity<ShareDtos.ShareResponse> sendToSelf(@PathVariable Long cardId,
                                                              @RequestHeader(name = "X-User-Id", required = false) String userHeader,
                                                              @RequestHeader(name = "Idempotency-Key", required = false) String idemKey) {
        UUID userId = resolveUserId(userHeader);
        // Idempotency: if already shared, return existing
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = sharesRepository.findFirstByFlowerCards_CardId(cardId);
            if (existing.isPresent()) {
                return ResponseEntity.ok(toResponse(existing.get()));
            }
        }
        Shares s = shareService.sendToSelf(userId, cardId);
        return ResponseEntity.ok(toResponse(s));
    }

    @PostMapping("/cards/{cardId}/send")
    public ResponseEntity<ShareDtos.ShareResponse> sendToFriend(@PathVariable Long cardId,
                                                                @Valid @RequestBody ShareDtos.SendShareRequest req,
                                                                @RequestHeader(name = "X-User-Id", required = false) String userHeader,
                                                                @RequestHeader(name = "Idempotency-Key", required = false) String idemKey) {
        UUID senderId = resolveUserId(userHeader);
        if (idemKey != null && !idemKey.isBlank()) {
            var existing = sharesRepository.findFirstByFlowerCards_CardId(cardId);
            if (existing.isPresent()) {
                return ResponseEntity.ok(toResponse(existing.get()));
            }
        }
        Shares s = shareService.sendToFriend(senderId, cardId, req.receiverId(), req.toName(), req.fromName(), req.note());
        return ResponseEntity.ok(toResponse(s));
    }

    @GetMapping("/archive")
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
                new ShareDtos.SimpleUser(s.getSender().getUserId(), s.getSender().getNickname()),
                new ShareDtos.SimpleUser(s.getReceiver().getUserId(), s.getReceiver().getNickname()),
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
}
