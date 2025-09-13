package com.ganadi.palmful.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "flowers")
public class Flower {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "color_hex", nullable = false, length = 7)
    private String colorHex;
    
    @Column(name = "meaning", columnDefinition = "TEXT")
    private String meaning;
    
    @Column(name = "asset_url", columnDefinition = "TEXT")
    private String assetUrl;
    
    // One-to-Many relationships
    @OneToMany(mappedBy = "flower", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BouquetFlower> bouquetFlowers = new ArrayList<>();
    
    // Constructors
    public Flower() {}
    
    public Flower(String name, String colorHex, String meaning, String assetUrl) {
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
    
    public List<BouquetFlower> getBouquetFlowers() {
        return bouquetFlowers;
    }
    
    public void setBouquetFlowers(List<BouquetFlower> bouquetFlowers) {
        this.bouquetFlowers = bouquetFlowers;
    }
}
