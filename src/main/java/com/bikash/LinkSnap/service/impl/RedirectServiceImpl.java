package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkClickEventDTO;
import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.service.LinkClickEventService;
import com.bikash.LinkSnap.service.RedirectService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

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
    public String resolveRedirectUrl(
            String host,
            String shortCode,
            String ipAddress,
            String referer,
            String userAgent,
            String utmSource,
            String utmMedium,
            String utmCampaign
    ) {
        Optional<Domain> domain = domainRepository.findByHost(host);
        Link link = domain
                .flatMap(d -> linkRepository.findByDomainIdAndShortCode(d.getId(), shortCode))
                .or(() -> linkRepository.findFirstByShortCodeAndDeletedAtIsNull(shortCode))
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
        if (link.getMaxClicks() != null && linkClickEventService.countClicks(link.getId()) >= link.getMaxClicks()) {
            throw new IllegalStateException("Link has reached max click limit");
        }

        LinkClickEventDTO event = new LinkClickEventDTO();
        event.setLinkId(link.getId());
        event.setClickedAt(LocalDateTime.now());
        event.setIpHash(hashIp(ipAddress));
        event.setReferer(referer);
        event.setUserAgent(userAgent);
        event.setDeviceType(resolveDeviceType(userAgent));
        event.setUtmSource(utmSource);
        event.setUtmMedium(utmMedium);
        event.setUtmCampaign(utmCampaign);
        event.setBot(false);
        linkClickEventService.recordClickEvent(event);

        return link.getOriginalUrl();
    }

    private String resolveDeviceType(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "mobile";
        }
        return "desktop";
    }

    private String hashIp(String ipAddress) {
        if (ipAddress == null || ipAddress.isBlank()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(ipAddress.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to hash IP address", e);
        }
    }
}
