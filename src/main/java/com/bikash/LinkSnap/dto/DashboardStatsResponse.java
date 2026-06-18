package com.bikash.LinkSnap.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DashboardStatsResponse {

    private Long totalLinks;
    private Long totalClicks;

    private Long activeLinks;

    private Long expiredLinks;
}