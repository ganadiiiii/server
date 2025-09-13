package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.OrderRequest;
import com.ganadi.palmful.dto.OrderResponse;
import com.ganadi.palmful.service.OrderService;
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
@RequestMapping("/api/orders")
@Tag(name = "주문", description = "주문 생성 및 조회 API")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "주문 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증 필요"),
        @ApiResponse(responseCode = "404", description = "부케 없음")
    })
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            // TODO: JWT에서 현재 사용자 ID 추출
            Long userId = 1L; // 임시
            OrderResponse response = orderService.createOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 상세 조회", description = "특정 주문의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주문 상세 반환"),
        @ApiResponse(responseCode = "404", description = "주문 없음"),
        @ApiResponse(responseCode = "401", description = "인증 필요")
    })
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/users/me/orders")
    @Operation(summary = "내 주문 목록 조회", description = "현재 사용자의 주문 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내 주문 목록 반환")
    })
    public ResponseEntity<java.util.List<OrderResponse>> getMyOrders() {
        // TODO: JWT에서 현재 사용자 ID 추출
        Long userId = 1L; // 임시
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }
}
