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
        event.setBrowser(resolveBrowser(userAgent));
        event.setOs(resolveOs(userAgent));
        event.setUtmSource(utmSource);
        event.setUtmMedium(utmMedium);
        event.setUtmCampaign(utmCampaign);
        event.setBot(isBot(userAgent, referer));
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

    private String resolveBrowser(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/")) return "edge";
        if (ua.contains("chrome/")) return "chrome";
        if (ua.contains("firefox/")) return "firefox";
        if (ua.contains("safari/") && !ua.contains("chrome/")) return "safari";
        if (ua.contains("opr/") || ua.contains("opera")) return "opera";
        return "other";
    }

    private String resolveOs(String userAgent) {
        if (userAgent == null) {
            return "unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) return "windows";
        if (ua.contains("mac os") || ua.contains("macintosh")) return "macos";
        if (ua.contains("android")) return "android";
        if (ua.contains("iphone") || ua.contains("ipad") || ua.contains("ios")) return "ios";
        if (ua.contains("linux")) return "linux";
        return "other";
    }

    private boolean isBot(String userAgent, String referer) {
        String ua = userAgent == null ? "" : userAgent.toLowerCase();
        String ref = referer == null ? "" : referer.toLowerCase();
        return ua.contains("bot")
                || ua.contains("spider")
                || ua.contains("crawler")
                || ua.contains("preview")
                || ref.contains("t.co/i/web/status")
                || ref.contains("slack.com")
                || ref.contains("discord.com");
    }
}
