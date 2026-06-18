package com.bikash.LinkSnap.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LinkResponse {

    private Long id;
    private String originalUrl;
    private String shortCode;
    private Long clickCount;
    private LocalDateTime createdAt;
}
