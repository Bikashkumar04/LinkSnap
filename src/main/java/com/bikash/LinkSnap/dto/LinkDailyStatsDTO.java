package com.bikash.LinkSnap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkDailyStatsDTO {

    private Long linkId;
    private LocalDate statDate;
    private long totalClicks;
    private long uniqueClicks;
    private long botClicks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
