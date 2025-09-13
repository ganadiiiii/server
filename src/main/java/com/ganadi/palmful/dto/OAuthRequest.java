package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "소셜 로그인 요청")
public class OAuthRequest {
    
    @NotBlank(message = "인증 코드는 필수입니다")
    @Schema(description = "OAuth 인증 코드", example = "4/0AX4XfWh...")
    private String code;
    
    @NotBlank(message = "리다이렉트 URI는 필수입니다")
    @Schema(description = "리다이렉트 URI", example = "http://localhost:3000/auth/callback")
    private String redirectUri;
    
    // Constructors
    public OAuthRequest() {}
    
    public OAuthRequest(String code, String redirectUri) {
        this.code = code;
        this.redirectUri = redirectUri;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getRedirectUri() {
        return redirectUri;
    }
    
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }
}
