package com.bikash.LinkSnap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QrCodeResponse {

    private String originalUrl;

    private String shortCode;

    private String shortUrl;

    private Long clickCount;
}
