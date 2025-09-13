package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "장바구니 아이템 응답")
public class CartItemResponse {
    
    @Schema(description = "장바구니 아이템 ID", example = "1")
    private Long id;
    
    @Schema(description = "부케 정보")
    private BouquetResponse bouquet;
    
    @Schema(description = "수량", example = "1")
    private Integer quantity;
    
    @Schema(description = "추가일시", example = "2024-01-01T00:00:00")
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
