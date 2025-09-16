package com.ganadi.palmful.dto;

import java.time.LocalDateTime;

public class CartItemResponse {
    
    private Long id;
    
    private BouquetResponse bouquet;
    
    private Integer quantity;
    
    private LocalDateTime createdAt;
    
    // Constructors
    public CartItemResponse() {}
    
    public CartItemResponse(Long id, BouquetResponse bouquet, Integer quantity, LocalDateTime createdAt) {
        this.id = id;
        this.bouquet = bouquet;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public BouquetResponse getBouquet() {
        return bouquet;
    }
    
    public void setBouquet(BouquetResponse bouquet) {
        this.bouquet = bouquet;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
