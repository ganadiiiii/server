package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "부케에 포함된 꽃 정보 응답")
public class BouquetFlowerResponse {
    
    @Schema(description = "꽃 정보")
    private FlowerResponse flower;
    
    @Schema(description = "수량", example = "3")
    private Integer quantity;
    
    // Constructors
    public BouquetFlowerResponse() {}
    
    public BouquetFlowerResponse(FlowerResponse flower, Integer quantity) {
        this.flower = flower;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public FlowerResponse getFlower() {
        return flower;
    }
    
    public void setFlower(FlowerResponse flower) {
        this.flower = flower;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
