package com.ganadi.palmful.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "bouquet_generations", indexes = {
    @Index(name = "idx_bouquet_generations_bouquet_version", columnList = "bouquet_id, version", unique = true)
})
public class BouquetGeneration {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bouquet_id", nullable = false)
    private Bouquet bouquet;
    
    @Column(name = "version", nullable = false)
    private Integer version;
    
    @Column(name = "model", length = 50)
    private String model;
    
    @Column(name = "prompt", columnDefinition = "TEXT")
    private String prompt;
    
    @Column(name = "seed")
    private Long seed;
    
    @Column(name = "params_json", columnDefinition = "TEXT")
    private String paramsJson;
    
    @Column(name = "preview_url", nullable = false, columnDefinition = "TEXT")
    private String previewUrl;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "generated";
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "items_json", columnDefinition = "TEXT")
    private String itemsJson;
    
    // Constructors
    public BouquetGeneration() {}
    
    public BouquetGeneration(Bouquet bouquet, Integer version, String model, String prompt, String previewUrl) {
        this.bouquet = bouquet;
        this.version = version;
        this.model = model;
        this.prompt = prompt;
        this.previewUrl = previewUrl;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Bouquet getBouquet() {
        return bouquet;
    }
    
    public void setBouquet(Bouquet bouquet) {
        this.bouquet = bouquet;
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
    
    public String getParamsJson() {
        return paramsJson;
    }
    
    public void setParamsJson(String paramsJson) {
        this.paramsJson = paramsJson;
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
    
    public String getItemsJson() {
        return itemsJson;
    }
    
    public void setItemsJson(String itemsJson) {
        this.itemsJson = itemsJson;
    }
}
