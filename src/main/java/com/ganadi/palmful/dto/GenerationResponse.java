package com.ganadi.palmful.dto;

import java.time.LocalDateTime;

public class GenerationResponse {
    
    private Long id;
    
    private Integer version;
    
    private String model;
    
    private String prompt;
    
    private Long seed;
    
    private String previewUrl;
    
    private String status;
    
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
