package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "장바구니 아이템 요청")
public class CartItemRequest {
    
    @NotNull(message = "부케 ID는 필수입니다")
    @Schema(description = "부케 ID", example = "1")
    private Long bouquetId;
    
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Schema(description = "수량", example = "1")
    private Integer quantity;
    
    // Constructors
    public CartItemRequest() {}
    
    public CartItemRequest(Long bouquetId, Integer quantity) {
        this.bouquetId = bouquetId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getBouquetId() {
        return bouquetId;
    }
    
    public void setBouquetId(Long bouquetId) {
        this.bouquetId = bouquetId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
