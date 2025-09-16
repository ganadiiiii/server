package com.ganadi.palmful.dto;


public class BouquetFlowerResponse {
    
    private FlowerResponse flower;
    
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
