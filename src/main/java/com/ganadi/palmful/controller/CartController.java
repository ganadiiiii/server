package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.CartItemRequest;
import com.ganadi.palmful.dto.CartItemResponse;
import com.ganadi.palmful.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "장바구니", description = "장바구니 조회 및 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "장바구니 조회", description = "현재 사용자의 장바구니 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "장바구니 목록")
    })
    public ResponseEntity<java.util.List<CartItemResponse>> getCartItems(Pageable pageable) {
        // TODO: JWT에서 현재 사용자 ID 추출
        Long userId = 1L; // 임시
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    @Operation(summary = "장바구니 담기", description = "꽃다발을 장바구니에 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "장바구니 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "부케 없음")
    })
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            // TODO: JWT에서 현재 사용자 ID 추출
            Long userId = 1L; // 임시
            CartItemResponse response = cartService.addItem(userId, request.getBouquetId(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "장바구니 아이템 삭제", description = "장바구니에서 특정 아이템을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "아이템 없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        try {
            cartService.removeItem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "장바구니 아이템 수량 수정", description = "장바구니 아이템의 수량을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수정 성공"),
        @ApiResponse(responseCode = "404", description = "아이템 없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Long id, @Valid @RequestBody CartItemRequest request) {
        // 선택사항: 업데이트 로직 필요 시 서비스 추가 구현
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}
