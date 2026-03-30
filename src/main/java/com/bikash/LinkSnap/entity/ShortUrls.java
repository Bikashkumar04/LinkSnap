package com.bikash.LinkSnap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "short_urls")
public class ShortUrls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", columnDefinition = "TEXT", nullable = false)
    private String originalUrl;

    @Column(name = "short_code", unique = true, nullable = false)
    private String shortCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expiry_at")
    private LocalDateTime expiryAt;

    @Column(name = "click_count")
    private int clickCount;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "is_active")
    private boolean isActive;

    public ShortUrls() {}

    public ShortUrls(String originalUrl, String shortCode,
                     LocalDateTime createdAt, LocalDateTime expiryAt,
                     int clickCount, Long userId, boolean isActive) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
        this.expiryAt = expiryAt;
        this.clickCount = clickCount;
        this.userId = userId;
        this.isActive = isActive;
    }

    // getters and setters
}