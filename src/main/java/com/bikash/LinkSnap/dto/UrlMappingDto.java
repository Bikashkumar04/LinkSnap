package com.bikash.LinkSnap.dto;

import lombok.Data;

@Data
public class UrlMappingDto {

    private String originalUrl;
    private String shortCode;
    private String shortUrl;
}