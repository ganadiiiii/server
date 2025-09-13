package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "친구 정보 응답")
public class FriendResponse {
    
    @Schema(description = "친구 사용자 정보")
    private UserResponse user;
    
    @Schema(description = "친구가 된 날짜", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    // Constructors
    public FriendResponse() {}
    
    public FriendResponse(UserResponse user, LocalDateTime createdAt) {
        this.user = user;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
