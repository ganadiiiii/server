package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "부케에 포함될 꽃 정보")
public class BouquetFlowerRequest {
    
    @NotNull(message = "꽃 ID는 필수입니다")
    @Schema(description = "꽃 ID", example = "1")
    private Long flowerId;
    
    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다")
    @Schema(description = "꽃 수량", example = "3")
    private Integer quantity;
    
    // Constructors
    public BouquetFlowerRequest() {}
    
    public BouquetFlowerRequest(Long flowerId, Integer quantity) {
        this.flowerId = flowerId;
        this.quantity = quantity;
    }
    
    // Getters and Setters
    public Long getFlowerId() {
        return flowerId;
    }
    
    public void setFlowerId(Long flowerId) {
        this.flowerId = flowerId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
