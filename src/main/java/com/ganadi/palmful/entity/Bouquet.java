package com.ganadi.palmful.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bouquets", indexes = {
    @Index(name = "idx_bouquets_owner", columnList = "owner_id")
})
public class Bouquet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @Column(name = "title", nullable = false, length = 120)
    private String title;
    
    @Column(name = "mood", length = 30)
    private String mood;
    
    @Column(name = "occasion", length = 30)
    private String occasion;
    
    @Column(name = "size", length = 20)
    private String size;
    
    @Column(name = "message", columnDefinition = "TEXT")
    private String message;
    
    @Column(name = "status", nullable = false, length = 20)
    private String status = "draft";
    
    @Column(name = "preview_url", columnDefinition = "TEXT")
    private String previewUrl;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    // One-to-Many relationships
    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BouquetFlower> bouquetFlowers = new ArrayList<>();
    
    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BouquetGeneration> bouquetGenerations = new ArrayList<>();
    
    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Gift> gifts = new ArrayList<>();
    
    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShareLink> shareLinks = new ArrayList<>();
    
    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    @OneToMany(mappedBy = "bouquet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();
    
    // Constructors
    public Bouquet() {}
    
    public Bouquet(User owner, String title, String mood, String occasion, String size, String message) {
        this.owner = owner;
        this.title = title;
        this.mood = mood;
        this.occasion = occasion;
        this.size = size;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
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
    
    public List<BouquetFlower> getBouquetFlowers() {
        return bouquetFlowers;
    }
    
    public void setBouquetFlowers(List<BouquetFlower> bouquetFlowers) {
        this.bouquetFlowers = bouquetFlowers;
    }
    
    public List<BouquetGeneration> getBouquetGenerations() {
        return bouquetGenerations;
    }
    
    public void setBouquetGenerations(List<BouquetGeneration> bouquetGenerations) {
        this.bouquetGenerations = bouquetGenerations;
    }
    
    public List<Gift> getGifts() {
        return gifts;
    }
    
    public void setGifts(List<Gift> gifts) {
        this.gifts = gifts;
    }
    
    public List<ShareLink> getShareLinks() {
        return shareLinks;
    }
    
    public void setShareLinks(List<ShareLink> shareLinks) {
        this.shareLinks = shareLinks;
    }
    
    public List<Order> getOrders() {
        return orders;
    }
    
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
}
