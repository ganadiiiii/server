package ganadii.hanjum.controller;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.dto.FriendDtos;
import ganadii.hanjum.service.FriendsService;
import ganadii.hanjum.web.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "05-친구", description = "친구 검색 및 관리")
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/friends")
    @Operation(summary = "친구 목록", description = "나의 친구 목록을 페이지로 조회합니다.")
    public ResponseEntity<FriendDtos.FriendListResponse> list(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        Page<User> p = friendsService.listFriends(userId, page, size);
        List<FriendDtos.FriendSummary> items = p.getContent().stream()
                .map(u -> new FriendDtos.FriendSummary(u.getUserId(), u.getEmail(), u.getFirstName(), u.getLastName(), friendsService.friendCount(u.getUserId())))
                .toList();
        return ResponseEntity.ok(new FriendDtos.FriendListResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()));
    }

    @GetMapping("/users/search")
    @Operation(summary = "사용자 검색", description = "친구 추가를 위해 사용자를 검색합니다.")
    public ResponseEntity<FriendDtos.SearchResponse> search(@RequestParam String q,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        Page<User> p = friendsService.searchUsers(userId, q, page, size);
        var friendIds = friendsService.friendIds(userId);
        List<FriendDtos.SearchItem> items = p.getContent().stream().map(u -> new FriendDtos.SearchItem(
                u.getUserId(), u.getEmail(), u.getFirstName(), u.getLastName(), friendIds.contains(u.getUserId()), friendsService.friendCount(u.getUserId())
        )).toList();
        return ResponseEntity.ok(new FriendDtos.SearchResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()));
    }

    @DeleteMapping("/friends/{friendId}")
    @Operation(summary = "친구 삭제", description = "선택한 친구 관계를 해제합니다.")
    public ResponseEntity<Void> delete(@PathVariable UUID friendId) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        friendsService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
