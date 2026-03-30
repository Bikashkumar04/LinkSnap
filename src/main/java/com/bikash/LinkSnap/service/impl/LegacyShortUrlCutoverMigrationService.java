package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.entity.ShortUrls;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.entity.Workspace;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.repository.ShortUrlRepository;
import com.bikash.LinkSnap.repository.UserRepository;
import com.bikash.LinkSnap.repository.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LegacyShortUrlCutoverMigrationService {

    private static final Logger log = LoggerFactory.getLogger(LegacyShortUrlCutoverMigrationService.class);

    private final ShortUrlRepository shortUrlRepository;
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final DomainRepository domainRepository;

    @Value("${linksnap.domain}")
    private String linksnapDomain;

    public LegacyShortUrlCutoverMigrationService(
            ShortUrlRepository shortUrlRepository,
            LinkRepository linkRepository,
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            DomainRepository domainRepository
    ) {
        this.shortUrlRepository = shortUrlRepository;
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.domainRepository = domainRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateLegacyDataIfNeeded() {
        List<ShortUrls> legacyLinks = shortUrlRepository.findAll();
        if (legacyLinks.isEmpty()) {
            return;
        }

        User fallbackUser = getOrCreateFallbackUser();
        Workspace fallbackWorkspace = getOrCreateWorkspace(
                fallbackUser.getId(),
                "legacy-migrated-links",
                "Legacy Migrated Links"
        );
        Domain defaultDomain = getOrCreateDefaultDomain(fallbackWorkspace.getId());

        int migrated = 0;
        for (ShortUrls legacy : legacyLinks) {
            if (legacy.getShortCode() == null || legacy.getShortCode().isBlank()) {
                continue;
            }
            if (linkRepository.existsByDomainIdAndShortCode(defaultDomain.getId(), legacy.getShortCode())) {
                continue;
            }

            Long ownerUserId = resolveOwnerUserId(legacy.getUserId(), fallbackUser.getId());
            Workspace ownerWorkspace = getOrCreateWorkspace(
                    ownerUserId,
                    "legacy-u-" + ownerUserId,
                    "Legacy Workspace U" + ownerUserId
            );

            Link link = new Link();
            link.setWorkspaceId(ownerWorkspace.getId());
            link.setDomainId(defaultDomain.getId());
            link.setCreatedByUserId(ownerUserId);
            link.setShortCode(legacy.getShortCode());
            link.setOriginalUrl(legacy.getOriginalUrl());
            link.setTitle(null);
            link.setActive(legacy.isActive());
            link.setExpiresAt(legacy.getExpiryAt());
            link.setMaxClicks(legacy.getMaxClicks());
            link.setDeletedAt(null);
            linkRepository.save(link);
            migrated++;
        }

        if (migrated > 0) {
            log.info("Legacy cutover migration completed. migrated_count={}", migrated);
        }
    }

    private Long resolveOwnerUserId(Long legacyUserId, Long fallbackUserId) {
        if (legacyUserId != null && userRepository.existsById(legacyUserId)) {
            return legacyUserId;
        }
        return fallbackUserId;
    }

    private User getOrCreateFallbackUser() {
        return userRepository.findByEmail("legacy-migration@linksnap.local")
                .orElseGet(() -> {
                    User user = new User();
                    user.setEmail("legacy-migration@linksnap.local");
                    user.setName("Legacy Migration");
                    user.setPasswordHash("NO_LOGIN");
                    user.setStatus("ACTIVE");
                    return userRepository.save(user);
                });
    }

    private Workspace getOrCreateWorkspace(Long ownerUserId, String slug, String name) {
        return workspaceRepository.findBySlug(slug)
                .orElseGet(() -> {
                    Workspace workspace = new Workspace();
                    workspace.setName(name);
                    workspace.setSlug(slug);
                    workspace.setOwnerUserId(ownerUserId);
                    workspace.setPlanTier("FREE");
                    return workspaceRepository.save(workspace);
                });
    }

    private Domain getOrCreateDefaultDomain(Long workspaceId) {
        String host = extractHost(linksnapDomain);
        return domainRepository.findByHost(host)
                .orElseGet(() -> {
                    Domain domain = new Domain();
                    domain.setWorkspaceId(workspaceId);
                    domain.setHost(host);
                    domain.setPrimary(true);
                    domain.setVerificationStatus("VERIFIED");
                    return domainRepository.save(domain);
                });
    }

    private String extractHost(String baseUrl) {
        try {
            URI uri = URI.create(baseUrl);
            if (uri.getHost() != null && !uri.getHost().isBlank()) {
                return uri.getHost();
            }
        } catch (Exception ignored) {
            // fallback below
        }
        String sanitized = baseUrl.replace("https://", "").replace("http://", "");
        int slash = sanitized.indexOf('/');
        sanitized = slash > 0 ? sanitized.substring(0, slash) : sanitized;
        int colon = sanitized.indexOf(':');
        return colon > 0 ? sanitized.substring(0, colon) : sanitized;
    }
}
