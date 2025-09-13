package com.ganadi.palmful.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "AI 생성 요청")
public class GenerationRequest {
    
    @Schema(description = "프롬프트", example = "아름다운 장미 부케")
    private String prompt;
    
    @Schema(description = "시드 값", example = "12345")
    private Long seed;
    
    @Schema(description = "생성 파라미터 JSON", example = "{\"steps\": 20, \"cfg_scale\": 7.5}")
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
