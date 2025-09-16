package com.ganadi.palmful.dto;


public class GenerationRequest {
    
    private String prompt;
    
    private Long seed;
    
    private String paramsJson;
    
    // Constructors
    public GenerationRequest() {}
    
    public GenerationRequest(String prompt, Long seed, String paramsJson) {
        this.prompt = prompt;
        this.seed = seed;
        this.paramsJson = paramsJson;
    }
    
    // Getters and Setters
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
    
    public String getParamsJson() {
        return paramsJson;
    }
    
    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
    }
}
