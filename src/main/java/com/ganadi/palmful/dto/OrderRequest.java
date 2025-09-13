package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "주문 요청")
public class OrderRequest {
    
    @NotNull(message = "부케 ID는 필수입니다")
    @Schema(description = "부케 ID", example = "1")
    private Long bouquetId;
    
    @NotNull(message = "총 가격은 필수입니다")
    @Min(value = 0, message = "가격은 0원 이상이어야 합니다")
    @Schema(description = "총 가격", example = "50000")
    private Integer totalPrice;
    
    @NotBlank(message = "받는 사람 이름은 필수입니다")
    @Size(max = 100, message = "받는 사람 이름은 100자를 초과할 수 없습니다")
    @Schema(description = "받는 사람 이름", example = "홍길동")
    private String recipientName;
    
    @NotBlank(message = "전화번호는 필수입니다")
    @Size(max = 20, message = "전화번호는 20자를 초과할 수 없습니다")
    @Schema(description = "전화번호", example = "010-1234-5678")
    private String phone;
    
    @NotBlank(message = "배송 주소는 필수입니다")
    @Schema(description = "배송 주소", example = "서울시 강남구 테헤란로 123")
    private String shippingAddr;
    
    // Constructors
    public OrderRequest() {}
    
    public OrderRequest(Long bouquetId, Integer totalPrice, String recipientName, String phone, String shippingAddr) {
        this.bouquetId = bouquetId;
        this.totalPrice = totalPrice;
        this.recipientName = recipientName;
        this.phone = phone;
        this.shippingAddr = shippingAddr;
    }
    
    // Getters and Setters
    public Long getBouquetId() {
        return bouquetId;
    }
    
    public void setBouquetId(Long bouquetId) {
        this.bouquetId = bouquetId;
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
}
