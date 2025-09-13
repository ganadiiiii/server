package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 응답")
public class AuthResponse {
    
    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "토큰 타입", example = "Bearer")
    private String tokenType = "Bearer";
    
    @Schema(description = "사용자 정보")
    private UserResponse user;
    
    // Constructors
    public AuthResponse() {}
    
    public AuthResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }
    
    public AuthResponse(String token, String tokenType, UserResponse user) {
        this.token = token;
        this.tokenType = tokenType;
        this.user = user;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public UserResponse getUser() {
        return user;
    }
    
    public void setUser(UserResponse user) {
        this.user = user;
    }
}
