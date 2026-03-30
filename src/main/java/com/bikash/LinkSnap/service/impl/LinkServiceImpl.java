package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkDTO;
import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.service.LinkService;
import com.bikash.LinkSnap.util.Base62Encoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LinkServiceImpl implements LinkService {

    private final LinkRepository linkRepository;
    private final DomainRepository domainRepository;

    @Value("${linksnap.domain}")
    private String defaultDomain;

    public LinkServiceImpl(LinkRepository linkRepository, DomainRepository domainRepository) {
        this.linkRepository = linkRepository;
        this.domainRepository = domainRepository;
    }

    @Override
    @Transactional
    public LinkDTO createLink(LinkDTO request) {
        String shortCode = request.getShortCode();
        if (shortCode == null || shortCode.isBlank()) {
            do {
                shortCode = Base62Encoder.generateRandom(6);
            } while (linkRepository.existsByDomainIdAndShortCode(request.getDomainId(), shortCode));
        } else {
            validateShortCode(shortCode);
            if (linkRepository.existsByDomainIdAndShortCode(request.getDomainId(), shortCode)) {
                throw new IllegalArgumentException("Short code already exists for this domain");
            }
        }

        Link link = new Link();
        link.setWorkspaceId(request.getWorkspaceId());
        link.setDomainId(request.getDomainId());
        link.setCreatedByUserId(request.getCreatedByUserId());
        link.setShortCode(shortCode);
        link.setOriginalUrl(request.getOriginalUrl());
        link.setTitle(request.getTitle());
        link.setActive(true);
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

    @Override
    @Transactional(readOnly = true)
    public String buildShortLink(Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));

        Domain domain = domainRepository.findById(link.getDomainId()).orElse(null);
        if (domain != null && domain.getHost() != null && !domain.getHost().isBlank()) {
            return "https://" + domain.getHost() + "/" + link.getShortCode();
        }
        return defaultDomain + "/" + link.getShortCode();
    }

    private void validateShortCode(String code) {
        if (!code.matches("^[a-zA-Z0-9_-]{4,20}$")) {
            throw new IllegalArgumentException(
                    "Short code must be 4-20 characters and contain only letters, numbers, - or _"
            );
        }
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
