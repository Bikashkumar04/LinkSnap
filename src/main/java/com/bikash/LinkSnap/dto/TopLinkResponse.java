package com.bikash.LinkSnap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TopLinkResponse {

    private Long id;
    private String shortCode;
    private String originalUrl;
    private Long clickCount;
}