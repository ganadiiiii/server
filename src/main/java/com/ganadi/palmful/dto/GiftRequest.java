package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "선물 요청")
public class GiftRequest {
    
    @NotNull(message = "부케 ID는 필수입니다")
    @Schema(description = "부케 ID", example = "1")
    private Long bouquetId;
    
    @NotNull(message = "받는 사람 ID는 필수입니다")
    @Schema(description = "받는 사람 ID", example = "2")
    private Long receiverId;
    
    @Size(max = 1000, message = "메시지는 1000자를 초과할 수 없습니다")
    @Schema(description = "선물 메시지", example = "생일 축하해!")
    private String message;
    
    // Constructors
    public GiftRequest() {}
    
    public GiftRequest(Long bouquetId, Long receiverId, String message) {
        this.bouquetId = bouquetId;
        this.receiverId = receiverId;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getBouquetId() {
        return bouquetId;
    }
    
    public void setBouquetId(Long bouquetId) {
        this.bouquetId = bouquetId;
    }
    
    public Long getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
