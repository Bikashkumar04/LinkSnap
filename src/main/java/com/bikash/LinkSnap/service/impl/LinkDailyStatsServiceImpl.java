package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkDailyStatsDTO;
import com.bikash.LinkSnap.entity.LinkDailyStats;
import com.bikash.LinkSnap.entity.LinkDailyStatsId;
import com.bikash.LinkSnap.repository.LinkDailyStatsRepository;
import com.bikash.LinkSnap.service.LinkDailyStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class LinkDailyStatsServiceImpl implements LinkDailyStatsService {

    private final LinkDailyStatsRepository linkDailyStatsRepository;

    public LinkDailyStatsServiceImpl(LinkDailyStatsRepository linkDailyStatsRepository) {
        this.linkDailyStatsRepository = linkDailyStatsRepository;
    }

    @Override
    @Transactional
    public LinkDailyStatsDTO upsertDailyStats(LinkDailyStatsDTO request) {
        LinkDailyStatsId id = new LinkDailyStatsId(request.getLinkId(), request.getStatDate());
        LinkDailyStats stats = linkDailyStatsRepository.findById(id).orElseGet(() -> {
            LinkDailyStats s = new LinkDailyStats();
            s.setId(id);
            return s;
        });
        stats.setTotalClicks(request.getTotalClicks());
        stats.setUniqueClicks(request.getUniqueClicks());
        stats.setBotClicks(request.getBotClicks());
        return toDTO(linkDailyStatsRepository.save(stats));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkDailyStatsDTO> getLinkStats(Long linkId, LocalDate from, LocalDate to) {
        return linkDailyStatsRepository.findByIdLinkIdAndIdStatDateBetween(linkId, from, to)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public void aggregateForDate(LocalDate date) {
        // Placeholder for scheduled aggregation from link_click_events to link_daily_stats.
        // Keep intentionally empty until aggregation pipeline is added.
    }

    private LinkDailyStatsDTO toDTO(LinkDailyStats stats) {
        return new LinkDailyStatsDTO(
                stats.getId().getLinkId(),
                stats.getId().getStatDate(),
                stats.getTotalClicks(),
                stats.getUniqueClicks(),
                stats.getBotClicks(),
                stats.getCreatedAt(),
                stats.getUpdatedAt()
        );
    }
}
