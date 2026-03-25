package com.bikash.LinkSnap.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class ShortUrls {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", columnDefinition = "TEXT")
    private String originalUrl;

    @Column(name = "short_code", unique = true)
    private String shortCode;

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Column(name = "expiry_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime expiryAt;

    @Column(name = "click_count")
    private int clickCount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_active")
    private boolean isActive;

    // Constructors, getters, and setters

    public ShortUrls() {
    }

    public ShortUrls(String originalUrl, String shortCode, LocalDateTime createdAt, LocalDateTime expiryAt, int clickCount, Long userId, boolean isActive) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.expiryAt = expiryAt;
        this.clickCount = clickCount;
        this.userId = userId;
        this.isActive = isActive;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiryAt() {
        return expiryAt;
    }

    public void setExpiryAt(LocalDateTime expiryAt) {
        this.expiryAt = expiryAt;
    }

    public int getClickCount() {
        return clickCount;
    }

    public void setClickCount(int clickCount) {
        this.clickCount = clickCount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
