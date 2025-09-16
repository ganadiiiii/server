package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.FriendRequestDto;
import com.ganadi.palmful.dto.FriendResponse;
import com.ganadi.palmful.service.FriendService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@Tag(name = "친구", description = "친구 요청/수락/거절 및 목록 조회")
public class FriendController {

    private final FriendService friendService;
    private final CurrentUserService currentUserService;

    @Autowired
    public FriendController(FriendService friendService, CurrentUserService currentUserService) {
        this.friendService = friendService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/requests")
    @Operation(summary = "친구 요청 보내기", description = "상대방에게 친구 요청을 생성합니다.")
    public ResponseEntity<Void> sendRequest(@Valid @RequestBody FriendRequestDto request) {
        try {
            Long requesterId = currentUserService.getCurrentUserId();
            friendService.sendRequest(requesterId, request.getAddresseeId(), request.getMessage());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/requests/{id}/accept")
    @Operation(summary = "친구 요청 수락", description = "수신자가 친구 요청을 수락합니다.")
    public ResponseEntity<Void> accept(@PathVariable("id") Long requestId) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            friendService.acceptRequest(requestId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/requests/{id}/deny")
    @Operation(summary = "친구 요청 거절", description = "수신자가 친구 요청을 거절합니다.")
    public ResponseEntity<Void> deny(@PathVariable("id") Long requestId) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            friendService.rejectRequest(requestId, userId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping
    @Operation(summary = "내 친구 목록", description = "내 친구 목록을 최신순으로 조회합니다.")
    public ResponseEntity<List<FriendResponse>> getFriends() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @GetMapping("/requests/received")
    @Operation(summary = "받은 친구 요청 목록", description = "미처리 요청을 최신순으로 조회합니다.")
    public ResponseEntity<List<FriendRequestDto>> getReceivedRequests() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(friendService.getReceivedRequests(userId));
    }

    @GetMapping("/requests/sent")
    @Operation(summary = "보낸 친구 요청 목록", description = "내가 보낸 요청을 최신순으로 조회합니다.")
    public ResponseEntity<List<FriendRequestDto>> getSentRequests() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(friendService.getSentRequests(userId));
    }

    @DeleteMapping("/{friendUserId}")
    public ResponseEntity<Void> removeFriend(@PathVariable Long friendUserId) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            friendService.removeFriend(userId, friendUserId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "친구 검색", description = "친구 목록에서 이름이나 이메일로 검색합니다.")
    public ResponseEntity<List<FriendResponse>> search(@RequestParam("q") String q) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            List<FriendResponse> results = friendService.searchFriends(userId, q);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
