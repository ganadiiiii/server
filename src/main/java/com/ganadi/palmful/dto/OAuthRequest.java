package com.ganadi.palmful.dto;

import jakarta.validation.constraints.NotBlank;

public class OAuthRequest {
    
    @NotBlank(message = "인증 코드는 필수입니다")
    private String code;
    
    @NotBlank(message = "리다이렉트 URI는 필수입니다")
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
