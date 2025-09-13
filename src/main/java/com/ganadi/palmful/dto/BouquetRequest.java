package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

@Schema(description = "부케 생성 요청")
public class BouquetRequest {
    
    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 120, message = "제목은 120자를 초과할 수 없습니다")
    @Schema(description = "부케 제목", example = "사랑하는 사람에게")
    private String title;
    
    @Size(max = 30, message = "무드는 30자를 초과할 수 없습니다")
    @Schema(description = "무드", example = "차분한")
    private String mood;
    
    @Size(max = 30, message = "상황은 30자를 초과할 수 없습니다")
    @Schema(description = "상황", example = "생일")
    private String occasion;
    
    @Size(max = 20, message = "크기는 20자를 초과할 수 없습니다")
    @Schema(description = "크기", example = "medium")
    private String size;
    
    @Schema(description = "메시지", example = "생일 축하해!")
    private String message;
    
    @NotEmpty(message = "꽃 목록은 필수입니다")
    @Valid
    @Schema(description = "포함될 꽃 목록")
    private List<BouquetFlowerRequest> flowers;
    
    // Constructors
    public BouquetRequest() {}
    
    public BouquetRequest(String title, String mood, String occasion, String size, String message, List<BouquetFlowerRequest> flowers) {
        this.title = title;
        this.mood = mood;
        this.occasion = occasion;
        this.size = size;
        this.message = message;
        this.flowers = flowers;
    }
    
    // Getters and Setters
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
    
    public List<BouquetFlowerRequest> getFlowers() {
        return flowers;
    }
    
    public void setFlowers(List<BouquetFlowerRequest> flowers) {
        this.flowers = flowers;
    }
}
