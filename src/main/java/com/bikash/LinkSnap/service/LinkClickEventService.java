package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.LinkClickEventDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface LinkClickEventService {

    LinkClickEventDTO recordClickEvent(LinkClickEventDTO eventDTO);

    List<LinkClickEventDTO> getLinkEvents(Long linkId);

    long countClicks(Long linkId);

    long countClicksBetween(Long linkId, LocalDateTime from, LocalDateTime to);
}
