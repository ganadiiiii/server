package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.service.BouquetService;
import com.ganadi.palmful.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자", description = "내 정보 및 내 리소스 조회")
public class UserController {
    
    private final BouquetService bouquetService;
    private final UserService userService;
    private final com.ganadi.palmful.service.CurrentUserService currentUserService;
    
    @Autowired
    public UserController(BouquetService bouquetService, UserService userService, com.ganadi.palmful.service.CurrentUserService currentUserService) {
        this.bouquetService = bouquetService;
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 인증된 사용자의 프로필 정보를 반환합니다.")
    public ResponseEntity<UserResponse> getMyInfo() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            return ResponseEntity.ok(userService.getUserById(userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PatchMapping("/me")
    @Operation(summary = "프로필 수정", description = "내 프로필 정보를 수정합니다.")
    public ResponseEntity<UserResponse> updateProfile(@Valid @RequestBody com.ganadi.palmful.dto.UserUpdateRequest request) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            UserResponse response = userService.updateUser(userId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/me/bouquets")
    @Operation(summary = "내 부케 목록", description = "내가 소유한 부케 목록을 반환합니다.")
    public ResponseEntity<List<BouquetResponse>> getMyBouquets() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            
            List<BouquetResponse> bouquets = bouquetService.getUserBouquets(userId);
            return ResponseEntity.ok(bouquets);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/me/orders")
    @Operation(summary = "내 주문 목록", description = "모의 주문 목록을 페이지로 반환합니다.")
    public ResponseEntity<Page<com.ganadi.palmful.dto.OrderResponse>> getMyOrders(Pageable pageable) {
        // TODO: 실제 주문 목록 조회 로직 구현
        return ResponseEntity.ok(Page.empty());
    }

    @GetMapping("/me/archives")
    @Operation(summary = "내 아카이브", description = "내가 만든 꽃다발과 받은 꽃다발을 아카이브에서 조회합니다.")
    public ResponseEntity<List<BouquetResponse>> getMyArchives() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            List<BouquetResponse> archives = bouquetService.getUserArchives(userId);
            return ResponseEntity.ok(archives);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(401).build();
        }
    }
}
