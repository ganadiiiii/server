package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "부케 정보 응답")
public class BouquetResponse {
    
    @Schema(description = "부케 ID", example = "1")
    private Long id;
    
    @Schema(description = "소유자 정보")
    private UserResponse owner;
    
    @Schema(description = "부케 제목", example = "사랑하는 사람에게")
    private String title;
    
    @Schema(description = "무드", example = "차분한")
    private String mood;
    
    @Schema(description = "상황", example = "생일")
    private String occasion;
    
    @Schema(description = "크기", example = "medium")
    private String size;
    
    @Schema(description = "메시지", example = "생일 축하해!")
    private String message;
    
    @Schema(description = "상태", example = "draft")
    private String status;
    
    @Schema(description = "미리보기 URL", example = "https://example.com/preview.jpg")
    private String previewUrl;
    
    @Schema(description = "포함된 꽃 목록")
    private List<BouquetFlowerResponse> flowers;
    
    @Schema(description = "생성일시", example = "2024-01-01T00:00:00")
    private LocalDateTime createdAt;
    
    @Schema(description = "수정일시", example = "2024-01-01T00:00:00")
    private LocalDateTime updatedAt;
    
    @Schema(description = "아카이브일시", example = "2024-01-01T00:00:00")
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
