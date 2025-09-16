package com.ganadi.palmful.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BouquetResponse {
    
    private Long id;
    
    private UserResponse owner;
    
    private String title;
    
    private String mood;
    
    private String occasion;
    
    private String size;
    
    private String message;
    
    private String status;
    
    private String previewUrl;
    
    private List<BouquetFlowerResponse> flowers;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime archivedAt;
    
    // Constructors
    public BouquetResponse() {}
    
    public BouquetResponse(Long id, UserResponse owner, String title, String mood, String occasion, String size, String message, String status, String previewUrl, List<BouquetFlowerResponse> flowers, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.title = title;
        this.mood = mood;
        this.occasion = occasion;
        this.size = size;
        this.message = message;
        this.status = status;
        this.previewUrl = previewUrl;
        this.flowers = flowers;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserResponse getOwner() {
        return owner;
    }
    
    public void setOwner(UserResponse owner) {
        this.owner = owner;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getMood() {
        return mood;
    }
    
    public void setMood(String mood) {
        this.mood = mood;
    }
    
    public String getOccasion() {
        return occasion;
    }
    
    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }
    
    public String getSize() {
        return size;
    }
    
    public void setSize(String size) {
        this.size = size;
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
    
    public String getPreviewUrl() {
        return previewUrl;
    }
    
    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
    
    public List<BouquetFlowerResponse> getFlowers() {
        return flowers;
    }
    
    public void setFlowers(List<BouquetFlowerResponse> flowers) {
        this.flowers = flowers;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }
    
    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
}
