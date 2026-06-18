package com.bikash.LinkSnap.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UrlAnalyticsResponse {

    private String originalUrl;
    private String shortCode;
    private Long clickCount;
    private LocalDateTime createdAt;
}