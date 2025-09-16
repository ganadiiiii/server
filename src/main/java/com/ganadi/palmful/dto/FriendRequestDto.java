package com.ganadi.palmful.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class FriendRequestDto {
    
    private Long id;
    
    private Long requesterId;
    
    @NotNull(message = "받는 사람 ID는 필수입니다")
    private Long addresseeId;
    
    @Size(max = 500, message = "메시지는 500자를 초과할 수 없습니다")
    private String message;
    
    private String status;
    
    private LocalDateTime createdAt;
    
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
