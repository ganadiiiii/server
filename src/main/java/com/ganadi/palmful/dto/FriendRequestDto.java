package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Schema(description = "친구 요청")
public class FriendRequestDto {
    
    @Schema(description = "요청 ID", example = "1")
    private Long id;
    
    @Schema(description = "요청자 ID", example = "1")
    private Long requesterId;
    
    @NotNull(message = "받는 사람 ID는 필수입니다")
    @Schema(description = "받는 사람 ID", example = "2")
    private Long addresseeId;
    
    @Size(max = 500, message = "메시지는 500자를 초과할 수 없습니다")
    @Schema(description = "친구 요청 메시지", example = "안녕하세요! 친구가 되고 싶어요.")
    private String message;
    
    @Schema(description = "요청 상태", example = "pending")
    private String status;
    
    @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "응답일시", example = "2024-01-01T12:00:00")
    private LocalDateTime respondedAt;
    
    // Constructors
    public FriendRequestDto() {}
    
    public FriendRequestDto(Long addresseeId, String message) {
        this.addresseeId = addresseeId;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRequesterId() {
        return requesterId;
    }
    
    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }
    
    public Long getAddresseeId() {
        return addresseeId;
    }
    
    public void setAddresseeId(Long addresseeId) {
        this.addresseeId = addresseeId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }
    
    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }
}
