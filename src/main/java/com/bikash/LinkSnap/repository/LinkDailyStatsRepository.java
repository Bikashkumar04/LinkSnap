package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.LinkDailyStats;
import com.bikash.LinkSnap.entity.LinkDailyStatsId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LinkDailyStatsRepository extends JpaRepository<LinkDailyStats, LinkDailyStatsId> {

    List<LinkDailyStats> findByIdLinkId(Long linkId);

    Optional<LinkDailyStats> findByIdLinkIdAndIdStatDate(Long linkId, LocalDate statDate);

    List<LinkDailyStats> findByIdLinkIdAndIdStatDateBetween(Long linkId, LocalDate from, LocalDate to);
}
