package ganadii.hanjum.controller;

import ganadii.hanjum.domain.User;
import ganadii.hanjum.dto.FriendDtos;
import ganadii.hanjum.service.FriendsService;
import ganadii.hanjum.web.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;

    @GetMapping("/friends")
    public ResponseEntity<FriendDtos.FriendListResponse> list(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "20") int size) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        Page<User> p = friendsService.listFriends(userId, page, size);
        List<FriendDtos.FriendSummary> items = p.getContent().stream()
                .map(u -> new FriendDtos.FriendSummary(u.getUserId(), u.getEmail(), u.getNickname(), friendsService.friendCount(u.getUserId())))
                .toList();
        return ResponseEntity.ok(new FriendDtos.FriendListResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()));
    }

    @GetMapping("/users/search")
    public ResponseEntity<FriendDtos.SearchResponse> search(@RequestParam String q,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "20") int size) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        Page<User> p = friendsService.searchUsers(userId, q, page, size);
        var friendIds = friendsService.friendIds(userId);
        List<FriendDtos.SearchItem> items = p.getContent().stream().map(u -> new FriendDtos.SearchItem(
                u.getUserId(), u.getEmail(), u.getNickname(), friendIds.contains(u.getUserId()), friendsService.friendCount(u.getUserId())
        )).toList();
        return ResponseEntity.ok(new FriendDtos.SearchResponse(items, p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages()));
    }

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<Void> delete(@PathVariable UUID friendId) {
        UUID userId = SecurityUtils.currentUserIdOrThrow();
        friendsService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
