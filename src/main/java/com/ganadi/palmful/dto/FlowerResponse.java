package com.ganadi.palmful.dto;


public class FlowerResponse {
    
    private Long id;
    
    private String name;
    
    private String colorHex;
    
    private String meaning;
    
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
