package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.service.BouquetService;
import com.ganadi.palmful.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자", description = "사용자 정보 및 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class UserController {
    
    private final BouquetService bouquetService;
    private final UserService userService;
    
    @Autowired
    public UserController(BouquetService bouquetService, UserService userService) {
        this.bouquetService = bouquetService;
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 반환"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<UserResponse> getMyInfo(@org.springframework.security.core.annotation.AuthenticationPrincipal Object principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String email = String.valueOf(principal);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/me/bouquets")
    @Operation(summary = "내가 만든 꽃다발 목록", description = "현재 사용자가 만든 꽃다발 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "꽃다발 목록 반환")
    })
    public ResponseEntity<List<BouquetResponse>> getMyBouquets() {
        try {
            // TODO: 실제로는 JWT에서 사용자 ID를 추출해야 함
            Long userId = 1L; // 임시로 1L 사용
            
            List<BouquetResponse> bouquets = bouquetService.getUserBouquets(userId);
            return ResponseEntity.ok(bouquets);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/me/orders")
    @Operation(summary = "내 주문 목록 조회", description = "현재 사용자의 주문 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내 주문 목록 반환")
    })
    public ResponseEntity<Page<com.ganadi.palmful.dto.OrderResponse>> getMyOrders(Pageable pageable) {
        // TODO: 실제 주문 목록 조회 로직 구현
        return ResponseEntity.ok(Page.empty());
    }
}
