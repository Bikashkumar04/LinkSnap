package com.bikash.LinkSnap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkClickEventDTO {

    private Long id;
    private Long linkId;
    private LocalDateTime clickedAt;
    private String ipHash;
    private String countryCode;
    private String region;
    private String city;
    private String referer;
    private String userAgent;
    private String deviceType;
    private String browser;
    private String os;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private boolean isBot;
}
