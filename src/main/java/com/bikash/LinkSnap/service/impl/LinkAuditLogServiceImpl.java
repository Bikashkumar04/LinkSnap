package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkAuditLogDTO;
import com.bikash.LinkSnap.entity.LinkAuditLog;
import com.bikash.LinkSnap.repository.LinkAuditLogRepository;
import com.bikash.LinkSnap.service.LinkAuditLogService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LinkAuditLogServiceImpl implements LinkAuditLogService {

    private final LinkAuditLogRepository linkAuditLogRepository;

    public LinkAuditLogServiceImpl(LinkAuditLogRepository linkAuditLogRepository) {
        this.linkAuditLogRepository = linkAuditLogRepository;
    }

    @Override
    @Transactional
    public LinkAuditLogDTO createAuditLog(LinkAuditLogDTO request) {
        LinkAuditLog log = new LinkAuditLog();
        log.setLinkId(request.getLinkId());
        log.setActorUserId(request.getActorUserId());
        log.setAction(request.getAction());
        log.setBeforeJson(request.getBeforeJson());
        log.setAfterJson(request.getAfterJson());
        return toDTO(linkAuditLogRepository.save(log));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkAuditLogDTO> listByLinkId(Long linkId) {
        return linkAuditLogRepository.findByLinkIdOrderByCreatedAtDesc(linkId, PageRequest.of(0, 500))
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private LinkAuditLogDTO toDTO(LinkAuditLog log) {
        return new LinkAuditLogDTO(
                log.getId(),
                log.getLinkId(),
                log.getActorUserId(),
                log.getAction(),
                log.getBeforeJson(),
                log.getAfterJson(),
                log.getCreatedAt()
        );
    }
}
