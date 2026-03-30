package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.LinkDailyStatsDTO;

import java.time.LocalDate;
import java.util.List;

public interface LinkDailyStatsService {

    LinkDailyStatsDTO upsertDailyStats(LinkDailyStatsDTO request);

    List<LinkDailyStatsDTO> getLinkStats(Long linkId, LocalDate from, LocalDate to);

    void aggregateForDate(LocalDate date);
}
