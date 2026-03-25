package com.bikash.LinkSnap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShortUrlDTO {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiryAt;
    private int clickCount;
    private Long userId;
    private boolean isActive;

}