package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.service.LinkClickEventService;
import com.bikash.LinkSnap.service.RedirectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class RedirectServiceImpl implements RedirectService {

    private final DomainRepository domainRepository;
    private final LinkRepository linkRepository;
    private final LinkClickEventService linkClickEventService;

    public RedirectServiceImpl(
            DomainRepository domainRepository,
            LinkRepository linkRepository,
            LinkClickEventService linkClickEventService
    ) {
        this.domainRepository = domainRepository;
        this.linkRepository = linkRepository;
        this.linkClickEventService = linkClickEventService;
    }

    @Override
    @Transactional
    public String resolveRedirectUrl(String host, String shortCode) {
        Domain domain = domainRepository.findByHost(host)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found"));

        Link link = linkRepository.findByDomainIdAndShortCode(domain.getId(), shortCode)
                .orElseThrow(() -> new IllegalArgumentException("Short URL not found"));

        if (!link.isActive()) {
            throw new IllegalStateException("Link is inactive");
        }
        if (link.getDeletedAt() != null) {
            throw new IllegalStateException("Link is deleted");
        }
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Link has expired");
        }

        // Placeholder hook: click event enrichment (IP, UA, UTM) should be added in controller/filter layer.
        linkClickEventService.countClicks(link.getId());

        return link.getOriginalUrl();
    }
}
