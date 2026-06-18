package com.bikash.LinkSnap.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ClickEventResponse {

    private LocalDateTime clickTime;
    private String ipAddress;
    private String userAgent;
}
