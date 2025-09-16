package com.ganadi.palmful.dto;

import java.time.LocalDateTime;

public class OrderResponse {
    
    private Long id;
    
    private UserResponse user;
    
    private BouquetResponse bouquet;
    
    private Long bouquetId;
    
    private String status;
    
    private Integer totalPrice;
    
    private String recipientName;
    
    private String phone;
    
    private String shippingAddr;
    
    private LocalDateTime createdAt;
    
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
