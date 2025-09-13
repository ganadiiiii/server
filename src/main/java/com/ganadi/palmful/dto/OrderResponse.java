package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "주문 정보 응답")
public class OrderResponse {
    
    @Schema(description = "주문 ID", example = "1")
    private Long id;
    
    @Schema(description = "주문자 정보")
    private UserResponse user;
    
    @Schema(description = "부케 정보")
    private BouquetResponse bouquet;
    
    @Schema(description = "부케 ID", example = "1")
    private Long bouquetId;
    
    @Schema(description = "주문 상태", example = "pending")
    private String status;
    
    @Schema(description = "총 가격", example = "50000")
    private Integer totalPrice;
    
    @Schema(description = "받는 사람 이름", example = "홍길동")
    private String recipientName;
    
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @Schema(description = "배송 주소", example = "서울시 강남구 테헤란로 123")
    private String shippingAddr;
    
    @Schema(description = "주문일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
    private LocalDateTime updatedAt;
    
    // Constructors
    public OrderResponse() {}
    
    public OrderResponse(Long id, UserResponse user, BouquetResponse bouquet, String status, Integer totalPrice, String recipientName, String phone, String shippingAddr, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.user = user;
        this.bouquet = bouquet;
        this.status = status;
        this.totalPrice = totalPrice;
        this.recipientName = recipientName;
        this.phone = phone;
        this.shippingAddr = shippingAddr;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    public BouquetResponse getBouquet() {
        return bouquet;
    }
    
    public void setBouquet(BouquetResponse bouquet) {
        this.bouquet = bouquet;
    }
    
    public Long getBouquetId() {
        return bouquetId;
    }
    
    public void setBouquetId(Long bouquetId) {
        this.bouquetId = bouquetId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getRecipientName() {
        return recipientName;
    }
    
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getShippingAddr() {
        return shippingAddr;
    }
    
    public void setShippingAddr(String shippingAddr) {
        this.shippingAddr = shippingAddr;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
