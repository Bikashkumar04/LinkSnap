package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkDailyStatsDTO;
import com.bikash.LinkSnap.entity.LinkClickEvent;
import com.bikash.LinkSnap.entity.LinkDailyStats;
import com.bikash.LinkSnap.entity.LinkDailyStatsId;
import com.bikash.LinkSnap.repository.LinkClickEventRepository;
import com.bikash.LinkSnap.repository.LinkDailyStatsRepository;
import com.bikash.LinkSnap.service.LinkDailyStatsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LinkDailyStatsServiceImpl implements LinkDailyStatsService {

    private final LinkDailyStatsRepository linkDailyStatsRepository;
    private final LinkClickEventRepository linkClickEventRepository;

    public LinkDailyStatsServiceImpl(
            LinkDailyStatsRepository linkDailyStatsRepository,
            LinkClickEventRepository linkClickEventRepository
    ) {
        this.linkDailyStatsRepository = linkDailyStatsRepository;
        this.linkClickEventRepository = linkClickEventRepository;
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
    @Transactional
    public void aggregateForDate(LocalDate date) {
        LocalDateTime from = date.atStartOfDay();
        LocalDateTime to = from.plusDays(1);

        List<LinkClickEvent> events = linkClickEventRepository.findByClickedAtBetween(from, to);
        Map<Long, List<LinkClickEvent>> byLink = events.stream()
                .collect(Collectors.groupingBy(LinkClickEvent::getLinkId));

        for (Map.Entry<Long, List<LinkClickEvent>> entry : byLink.entrySet()) {
            Long linkId = entry.getKey();
            List<LinkClickEvent> linkEvents = entry.getValue();

            long totalClicks = linkEvents.size();
            long botClicks = linkEvents.stream().filter(LinkClickEvent::isBot).count();

            Set<String> uniqueVisitorKeys = new HashSet<>();
            for (LinkClickEvent event : linkEvents) {
                if (event.getIpHash() != null && !event.getIpHash().isBlank()) {
                    uniqueVisitorKeys.add(event.getIpHash());
                } else {
                    uniqueVisitorKeys.add("event:" + event.getId());
                }
            }
            long uniqueClicks = uniqueVisitorKeys.size();

            LinkDailyStatsId id = new LinkDailyStatsId(linkId, date);
            LinkDailyStats stats = linkDailyStatsRepository.findById(id).orElseGet(() -> {
                LinkDailyStats s = new LinkDailyStats();
                s.setId(id);
                return s;
            });
            stats.setTotalClicks(totalClicks);
            stats.setUniqueClicks(uniqueClicks);
            stats.setBotClicks(botClicks);
            linkDailyStatsRepository.save(stats);
        }
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
