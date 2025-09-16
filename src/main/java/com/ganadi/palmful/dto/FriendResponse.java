package com.ganadi.palmful.dto;

import java.time.LocalDateTime;

public class FriendResponse {
    
    private UserResponse user;
    
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
