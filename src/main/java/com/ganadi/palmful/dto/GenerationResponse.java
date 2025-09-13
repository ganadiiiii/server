package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "AI 생성 결과 응답")
public class GenerationResponse {
    
    @Schema(description = "생성 ID", example = "1")
    private Long id;
    
    @Schema(description = "버전", example = "1")
    private Integer version;
    
    @Schema(description = "모델명", example = "stable-diffusion")
    private String model;
    
    @Schema(description = "프롬프트", example = "아름다운 장미 부케")
    private String prompt;
    
    @Schema(description = "시드 값", example = "12345")
    private Long seed;
    
    @Schema(description = "미리보기 URL", example = "https://example.com/generated.jpg")
    private String previewUrl;
    
    @Schema(description = "상태", example = "generated")
    private String status;
    
    @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    // Constructors
    public GenerationResponse() {}
    
    public GenerationResponse(Long id, Integer version, String model, String prompt, Long seed, String previewUrl, String status, LocalDateTime createdAt) {
        this.id = id;
        this.version = version;
        this.model = model;
        this.prompt = prompt;
        this.seed = seed;
        this.previewUrl = previewUrl;
        this.status = status;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public String getPrompt() {
        return prompt;
    }
    
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
    
    public Long getSeed() {
        return seed;
    }
    
    public void setSeed(Long seed) {
        this.seed = seed;
    }
    
    public String getPreviewUrl() {
        return previewUrl;
    }
    
    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
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
}
