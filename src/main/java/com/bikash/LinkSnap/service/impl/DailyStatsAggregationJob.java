package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.service.LinkDailyStatsService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyStatsAggregationJob {

    private final LinkDailyStatsService linkDailyStatsService;

    public DailyStatsAggregationJob(LinkDailyStatsService linkDailyStatsService) {
        this.linkDailyStatsService = linkDailyStatsService;
    }

    @Scheduled(cron = "${linksnap.analytics.daily-cron:0 15 0 * * *}")
    public void aggregatePreviousDayStats() {
        linkDailyStatsService.aggregateForDate(LocalDate.now().minusDays(1));
    }
}
