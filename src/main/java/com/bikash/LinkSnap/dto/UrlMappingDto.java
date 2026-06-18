package com.bikash.LinkSnap.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UrlMappingDto {

    private String originalUrl;

    private String customAlias;

    private ExpiryType expiryType;

    private LocalDateTime customExpiryAt;

    private String shortCode;

    private String shortUrl;
}