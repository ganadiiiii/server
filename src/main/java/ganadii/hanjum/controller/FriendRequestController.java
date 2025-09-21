package ganadii.hanjum.controller;

import ganadii.hanjum.domain.FriendRequest;
import ganadii.hanjum.dto.FriendRequestDtos;
import ganadii.hanjum.service.FriendRequestService;
import ganadii.hanjum.web.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends/requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping
    public ResponseEntity<FriendRequestDtos.FriendRequestResponse> create(@Valid @RequestBody FriendRequestDtos.CreateRequest req) {
        UUID senderId = SecurityUtils.currentUserIdOrThrow();
        FriendRequest fr = friendRequestService.create(senderId, req.receiverId());
        return ResponseEntity.ok(toResponse(fr));
    }

    @GetMapping
    public ResponseEntity<FriendRequestDtos.ListResponse> list(@RequestParam(defaultValue = "received") String type) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        List<FriendRequest> list = friendRequestService.list(userId, type);
        return ResponseEntity.ok(new FriendRequestDtos.ListResponse(list.stream().map(this::toResponse).toList()));
    }

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<FriendRequestDtos.FriendRequestResponse> accept(@PathVariable Long requestId) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        FriendRequest fr = friendRequestService.accept(requestId, userId);
        return ResponseEntity.ok(toResponse(fr));
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<FriendRequestDtos.FriendRequestResponse> reject(@PathVariable Long requestId) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        FriendRequest fr = friendRequestService.reject(requestId, userId);
        return ResponseEntity.ok(toResponse(fr));
    }

    private FriendRequestDtos.FriendRequestResponse toResponse(FriendRequest r) {
        return new FriendRequestDtos.FriendRequestResponse(
                r.getRequestId(),
                new FriendRequestDtos.UserSummary(r.getSender().getUserId(), r.getSender().getEmail(), r.getSender().getNickname()),
                new FriendRequestDtos.UserSummary(r.getReceiver().getUserId(), r.getReceiver().getEmail(), r.getReceiver().getNickname()),
                r.getStatus(),
                r.getCreatedAt(),
                r.getRespondedAt()
        );
    }
}

