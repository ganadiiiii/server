package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.FriendRequestDto;
import com.ganadi.palmful.dto.FriendResponse;
import com.ganadi.palmful.service.FriendService;
import com.ganadi.palmful.service.CurrentUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@Tag(name = "친구", description = "친구 요청/관리 API")
@SecurityRequirement(name = "bearerAuth")
public class FriendController {

    private final FriendService friendService;
    private final CurrentUserService currentUserService;

    @Autowired
    public FriendController(FriendService friendService, CurrentUserService currentUserService) {
        this.friendService = friendService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/requests")
    @Operation(summary = "친구 요청 보내기", description = "상대에게 친구 요청을 보냅니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "요청 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
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
    @Operation(summary = "친구 요청 수락", description = "수락 시 양방향 친구가 생성됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "수락 완료"),
            @ApiResponse(responseCode = "404", description = "요청 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
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
    @Operation(summary = "친구 요청 거절", description = "요청을 거절합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "거절 완료"),
            @ApiResponse(responseCode = "404", description = "요청 없음"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
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
    @Operation(summary = "내 친구 목록", description = "현재 사용자의 친구 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목록 반환")
    })
    public ResponseEntity<List<FriendResponse>> getFriends() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(friendService.getFriends(userId));
    }

    @GetMapping("/requests/received")
    @Operation(summary = "받은 친구 요청 목록", description = "현재 사용자가 받은 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 목록 반환")
    })
    public ResponseEntity<List<FriendRequestDto>> getReceivedRequests() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(friendService.getReceivedRequests(userId));
    }

    @GetMapping("/requests/sent")
    @Operation(summary = "보낸 친구 요청 목록", description = "현재 사용자가 보낸 친구 요청 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 목록 반환")
    })
    public ResponseEntity<List<FriendRequestDto>> getSentRequests() {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(friendService.getSentRequests(userId));
    }

    @DeleteMapping("/{friendUserId}")
    @Operation(summary = "친구 삭제", description = "상대와 친구 관계를 해제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
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
    @Operation(summary = "친구 검색", description = "이메일이나 이름으로 사용자를 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    })
    public ResponseEntity<List<FriendResponse>> search(@RequestParam("q") String q) {
        // TODO: 실제 검색 구현 - UserRepository에 검색 쿼리 추가 필요
        return ResponseEntity.ok(List.of());
    }
}
