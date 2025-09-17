package com.ganadi.palmful.controller;

import com.ganadi.palmful.dto.OrderRequest;
import com.ganadi.palmful.dto.OrderResponse;
import com.ganadi.palmful.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@Tag(name = "주문", description = "주문 생성 및 조회(모의)")
public class OrderController {

    private final OrderService orderService;
    private final com.ganadi.palmful.service.CurrentUserService currentUserService;

    @Autowired
    public OrderController(OrderService orderService, com.ganadi.palmful.service.CurrentUserService currentUserService) {
        this.orderService = orderService;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    @Operation(summary = "내 주문 목록 조회", description = "내가 생성한 주문 목록을 조회합니다.")
    public ResponseEntity<java.util.List<OrderResponse>> getMyOrders() {
        try {
            Long userId = currentUserService.getCurrentUserId();
            java.util.List<OrderResponse> orders = orderService.getUserOrders(userId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    @Operation(summary = "주문 생성", description = "모의 주문을 생성합니다.")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            Long userId = currentUserService.getCurrentUserId();
            OrderResponse response = orderService.createOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "주문 조회", description = "주문 ID로 상세 정보를 조회합니다.")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(orderService.getOrder(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
