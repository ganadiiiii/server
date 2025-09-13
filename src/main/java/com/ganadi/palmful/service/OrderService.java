package com.ganadi.palmful.service;

import com.ganadi.palmful.dto.BouquetResponse;
import com.ganadi.palmful.dto.OrderRequest;
import com.ganadi.palmful.dto.OrderResponse;
import com.ganadi.palmful.dto.UserResponse;
import com.ganadi.palmful.entity.Bouquet;
import com.ganadi.palmful.entity.Order;
import com.ganadi.palmful.entity.User;
import com.ganadi.palmful.repository.BouquetRepository;
import com.ganadi.palmful.repository.OrderRepository;
import com.ganadi.palmful.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BouquetRepository bouquetRepository;
    private final UserRepository userRepository;
    private final BouquetService bouquetService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        BouquetRepository bouquetRepository,
                        UserRepository userRepository,
                        BouquetService bouquetService) {
        this.orderRepository = orderRepository;
        this.bouquetRepository = bouquetRepository;
        this.userRepository = userRepository;
        this.bouquetService = bouquetService;
    }

    @Transactional
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다: " + userId));
        Bouquet bouquet = bouquetRepository.findById(request.getBouquetId())
                .orElseThrow(() -> new IllegalArgumentException("부케를 찾을 수 없습니다: " + request.getBouquetId()));

        Order order = new Order(user, bouquet, request.getTotalPrice(), request.getRecipientName(), request.getPhone(), request.getShippingAddr());
        order.setStatus("pending");
        Order saved = orderRepository.save(order);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다: " + id));
        return toResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order order) {
        BouquetResponse bouquet = bouquetService.getBouquet(order.getBouquet().getId());
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setBouquetId(bouquet.getId());
        response.setTotalPrice(order.getTotalPrice());
        response.setRecipientName(order.getRecipientName());
        response.setPhone(order.getPhone());
        response.setShippingAddr(order.getShippingAddr());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}




