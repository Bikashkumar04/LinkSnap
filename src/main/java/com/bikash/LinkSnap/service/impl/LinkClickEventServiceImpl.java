package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkClickEventDTO;
import com.bikash.LinkSnap.entity.LinkClickEvent;
import com.bikash.LinkSnap.repository.LinkClickEventRepository;
import com.bikash.LinkSnap.service.LinkClickEventService;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkClickEventServiceImpl implements LinkClickEventService {

    private final LinkClickEventRepository linkClickEventRepository;

    public LinkClickEventServiceImpl(LinkClickEventRepository linkClickEventRepository) {
        this.linkClickEventRepository = linkClickEventRepository;
    }

    @Override
    @Transactional
    public LinkClickEventDTO recordClickEvent(LinkClickEventDTO eventDTO) {
        LinkClickEvent event = new LinkClickEvent();
        event.setLinkId(eventDTO.getLinkId());
        event.setClickedAt(eventDTO.getClickedAt());
        event.setIpHash(eventDTO.getIpHash());
        event.setCountryCode(eventDTO.getCountryCode());
        event.setRegion(eventDTO.getRegion());
        event.setCity(eventDTO.getCity());
        event.setReferer(eventDTO.getReferer());
        event.setUserAgent(eventDTO.getUserAgent());
        event.setDeviceType(eventDTO.getDeviceType());
        event.setBrowser(eventDTO.getBrowser());
        event.setOs(eventDTO.getOs());
        event.setUtmSource(eventDTO.getUtmSource());
        event.setUtmMedium(eventDTO.getUtmMedium());
        event.setUtmCampaign(eventDTO.getUtmCampaign());
        event.setBot(eventDTO.isBot());
        return toDTO(linkClickEventRepository.save(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkClickEventDTO> getLinkEvents(Long linkId) {
        return linkClickEventRepository.findByLinkId(linkId, Pageable.unpaged())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countClicks(Long linkId) {
        return linkClickEventRepository.countByLinkId(linkId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countClicksBetween(Long linkId, LocalDateTime from, LocalDateTime to) {
        return linkClickEventRepository.countByLinkIdAndClickedAtBetween(linkId, from, to);
    }

    private LinkClickEventDTO toDTO(LinkClickEvent event) {
        return new LinkClickEventDTO(
                event.getId(),
                event.getLinkId(),
                event.getClickedAt(),
                event.getIpHash(),
                event.getCountryCode(),
                event.getRegion(),
                event.getCity(),
                event.getReferer(),
                event.getUserAgent(),
                event.getDeviceType(),
                event.getBrowser(),
                event.getOs(),
                event.getUtmSource(),
                event.getUtmMedium(),
                event.getUtmCampaign(),
                event.isBot()
        );
    }
}
