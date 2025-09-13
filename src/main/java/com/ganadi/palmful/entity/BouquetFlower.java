package com.ganadi.palmful.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bouquet_flowers", indexes = {
    @Index(name = "idx_bouquet_flowers_bouquet", columnList = "bouquet_id"),
    @Index(name = "idx_bouquet_flowers_bouquet_flower", columnList = "bouquet_id, flower_id", unique = true)
})
public class BouquetFlower {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bouquet_id", nullable = false)
    private Bouquet bouquet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flower_id", nullable = false)
    private Flower flower;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    // Constructors
    public BouquetFlower() {}
    
    public BouquetFlower(Bouquet bouquet, Flower flower, Integer quantity) {
        this.bouquet = bouquet;
        this.flower = flower;
        this.quantity = quantity;
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
    
    public Flower getFlower() {
        return flower;
    }
    
    public void setFlower(Flower flower) {
        this.flower = flower;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
