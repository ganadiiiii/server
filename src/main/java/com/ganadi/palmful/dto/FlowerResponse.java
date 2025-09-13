package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "꽃 정보 응답")
public class FlowerResponse {
    
    @Schema(description = "꽃 ID", example = "1")
    private Long id;
    
    @Schema(description = "꽃 이름", example = "장미")
    private String name;
    
    @Schema(description = "색상 코드", example = "#FF0000")
    private String colorHex;
    
    @Schema(description = "꽃의 의미", example = "사랑과 열정")
    private String meaning;
    
    @Schema(description = "이미지 URL", example = "https://example.com/rose.jpg")
    private String assetUrl;
    
    // Constructors
    public FlowerResponse() {}
    
    public FlowerResponse(Long id, String name, String colorHex, String meaning, String assetUrl) {
        this.id = id;
        this.name = name;
        this.colorHex = colorHex;
        this.meaning = meaning;
        this.assetUrl = assetUrl;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColorHex() {
        return colorHex;
    }
    
    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }
    
    public String getMeaning() {
        return meaning;
    }
    
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    
    public String getAssetUrl() {
        return assetUrl;
    }
    
    public void setAssetUrl(String assetUrl) {
        this.assetUrl = assetUrl;
    }
}
