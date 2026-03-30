package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkDTO;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.service.LinkService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;

    public LinkServiceImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    @Transactional
    public LinkDTO createLink(LinkDTO request) {
        if (linkRepository.existsByDomainIdAndShortCode(request.getDomainId(), request.getShortCode())) {
            throw new IllegalArgumentException("Short code already exists for this domain");
        }
        Link link = new Link();
        link.setWorkspaceId(request.getWorkspaceId());
        link.setDomainId(request.getDomainId());
        link.setCreatedByUserId(request.getCreatedByUserId());
        link.setShortCode(request.getShortCode());
        link.setOriginalUrl(request.getOriginalUrl());
        link.setTitle(request.getTitle());
        link.setActive(request.isActive());
        link.setExpiresAt(request.getExpiresAt());
        link.setMaxClicks(request.getMaxClicks());
        return toDTO(linkRepository.save(link));
    }

    @Override
    @Transactional
    public LinkDTO updateLink(Long linkId, LinkDTO request) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        if (request.getOriginalUrl() != null) {
            link.setOriginalUrl(request.getOriginalUrl());
        }
        if (request.getTitle() != null) {
            link.setTitle(request.getTitle());
        }
        link.setExpiresAt(request.getExpiresAt());
        link.setMaxClicks(request.getMaxClicks());
        return toDTO(linkRepository.save(link));
    }

    @Override
    @Transactional
    public LinkDTO pauseLink(Long linkId, boolean active) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        link.setActive(active);
        return toDTO(linkRepository.save(link));
    }

    @Override
    @Transactional
    public void deleteLink(Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        link.setDeletedAt(LocalDateTime.now());
        linkRepository.save(link);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkDTO getLinkById(Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        return toDTO(link);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LinkDTO> listWorkspaceLinks(Long workspaceId) {
        return linkRepository.findByWorkspaceIdAndDeletedAtIsNull(workspaceId, org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private LinkDTO toDTO(Link link) {
        return new LinkDTO(
                link.getId(),
                link.getWorkspaceId(),
                link.getDomainId(),
                link.getCreatedByUserId(),
                link.getShortCode(),
                link.getOriginalUrl(),
                link.getTitle(),
                link.isActive(),
                link.getExpiresAt(),
                link.getMaxClicks(),
                link.getCreatedAt(),
                link.getUpdatedAt(),
                link.getDeletedAt()
        );
    }
}
