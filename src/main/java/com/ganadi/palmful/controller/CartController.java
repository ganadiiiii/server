package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.CartItemRequest;
import com.ganadi.palmful.dto.CartItemResponse;
import com.ganadi.palmful.dto.CartItemUpdateRequest;
import com.ganadi.palmful.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "장바구니", description = "장바구니 담기/조회/삭제(모의)")
public class CartController {

    private final CartService cartService;
    private final com.ganadi.palmful.service.CurrentUserService currentUserService;

    public CartController(CartService cartService, com.ganadi.palmful.service.CurrentUserService currentUserService) {
        this.cartService = cartService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "장바구니 조회", description = "내 장바구니 아이템 목록을 조회합니다.")
    public ResponseEntity<java.util.List<CartItemResponse>> getCartItems(Pageable pageable) {
        Long userId = currentUserService.getCurrentUserId();
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @PostMapping
    @Operation(summary = "장바구니 담기", description = "부케를 장바구니에 추가합니다.")
    public ResponseEntity<CartItemResponse> addToCart(@Valid @RequestBody CartItemRequest request) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            CartItemResponse response = cartService.addItem(userId, request.getBouquetId(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "장바구니 항목 삭제", description = "아이템 ID로 장바구니에서 제거합니다.")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long id) {
        try {
            cartService.removeItem(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "장바구니 항목 수정", description = "장바구니 아이템의 수량을 수정합니다.")
    public ResponseEntity<CartItemResponse> updateCartItem(@PathVariable Long id, @Valid @RequestBody CartItemUpdateRequest request) {
        try {
            CartItemResponse response = cartService.updateItem(id, request.getQuantity());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
