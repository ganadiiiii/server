package ganadii.hanjum.controller;

import ganadii.hanjum.domain.FriendRequest;
import ganadii.hanjum.dto.FriendRequestDtos;
import ganadii.hanjum.service.FriendRequestService;
import ganadii.hanjum.web.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends/requests")
@RequiredArgsConstructor
@Tag(name = "06-친구요청", description = "친구 요청 관리")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping
    @Operation(summary = "친구 요청 보내기", description = "다른 사용자에게 친구 요청을 전송합니다.")
    public ResponseEntity<FriendRequestDtos.FriendRequestResponse> create(@Valid @RequestBody FriendRequestDtos.CreateRequest req) {
        UUID senderId = SecurityUtils.currentUserIdOrThrow();
        FriendRequest fr = friendRequestService.create(senderId, req.receiverId());
        return ResponseEntity.ok(toResponse(fr));
    }

    @GetMapping
    @Operation(summary = "요청 목록", description = "보낸/받은 친구 요청을 조회합니다.")
    public ResponseEntity<FriendRequestDtos.ListResponse> list(@RequestParam(defaultValue = "received") String type) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        List<FriendRequest> list = friendRequestService.list(userId, type);
        return ResponseEntity.ok(new FriendRequestDtos.ListResponse(list.stream().map(this::toResponse).toList()));
    }

    @PostMapping("/{requestId}/accept")
    @Operation(summary = "친구 요청 수락", description = "친구 요청을 수락하고 친구 관계를 생성합니다.")
    public ResponseEntity<FriendRequestDtos.FriendRequestResponse> accept(@PathVariable Long requestId) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        FriendRequest fr = friendRequestService.accept(requestId, userId);
        return ResponseEntity.ok(toResponse(fr));
    }

    @PostMapping("/{requestId}/reject")
    @Operation(summary = "친구 요청 거절", description = "친구 요청을 거절합니다.")
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
