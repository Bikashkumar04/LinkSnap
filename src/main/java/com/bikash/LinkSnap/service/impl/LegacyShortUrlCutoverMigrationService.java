package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.entity.Workspace;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.repository.UserRepository;
import com.bikash.LinkSnap.repository.WorkspaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.List;

@Component
public class LegacyShortUrlCutoverMigrationService {

    private static final Logger log = LoggerFactory.getLogger(LegacyShortUrlCutoverMigrationService.class);

    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final WorkspaceRepository workspaceRepository;
    private final DomainRepository domainRepository;
    private final JdbcTemplate jdbcTemplate;

    @Value("${linksnap.domain}")
    private String linksnapDomain;

    public LegacyShortUrlCutoverMigrationService(
            LinkRepository linkRepository,
            UserRepository userRepository,
            WorkspaceRepository workspaceRepository,
            DomainRepository domainRepository,
            JdbcTemplate jdbcTemplate
    ) {
        this.linkRepository = linkRepository;
        this.userRepository = userRepository;
        this.workspaceRepository = workspaceRepository;
        this.domainRepository = domainRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void migrateLegacyDataIfNeeded() {
        if (wasCutoverAlreadySuccessful()) {
            return;
        }

        List<LegacyShortUrlRow> legacyLinks = jdbcTemplate.query(
                """
                SELECT id, original_url, short_code, expiry_at, click_count, max_clicks, user_id, is_active
                FROM short_urls
                """,
                (rs, rowNum) -> new LegacyShortUrlRow(
                        rs.getLong("id"),
                        rs.getString("original_url"),
                        rs.getString("short_code"),
                        rs.getTimestamp("expiry_at") == null ? null : rs.getTimestamp("expiry_at").toLocalDateTime(),
                        rs.getInt("click_count"),
                        rs.getObject("max_clicks") == null ? null : rs.getInt("max_clicks"),
                        rs.getObject("user_id") == null ? null : rs.getLong("user_id"),
                        rs.getBoolean("is_active")
                )
        );
        if (legacyLinks.isEmpty()) {
            recordAudit("SUCCESS", 0, "No legacy rows found");
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
        for (LegacyShortUrlRow legacy : legacyLinks) {
            if (legacy.shortCode() == null || legacy.shortCode().isBlank()) {
                continue;
            }
            if (linkRepository.existsByDomainIdAndShortCode(defaultDomain.getId(), legacy.shortCode())) {
                continue;
            }

            Long ownerUserId = resolveOwnerUserId(legacy.userId(), fallbackUser.getId());
            Workspace ownerWorkspace = getOrCreateWorkspace(
                    ownerUserId,
                    "legacy-u-" + ownerUserId,
                    "Legacy Workspace U" + ownerUserId
            );

            Link link = new Link();
            link.setWorkspaceId(ownerWorkspace.getId());
            link.setDomainId(defaultDomain.getId());
            link.setCreatedByUserId(ownerUserId);
            link.setShortCode(legacy.shortCode());
            link.setOriginalUrl(legacy.originalUrl());
            link.setTitle(null);
            link.setActive(legacy.active());
            link.setExpiresAt(legacy.expiryAt());
            link.setMaxClicks(legacy.maxClicks());
            link.setDeletedAt(null);
            linkRepository.save(link);
            migrated++;
        }

        recordAudit("SUCCESS", migrated, "Legacy cutover migration completed");
        log.info("Legacy cutover migration completed. migrated_count={}", migrated);
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

    private boolean wasCutoverAlreadySuccessful() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM cutover_migration_audit WHERE migration_name = ? AND status = 'SUCCESS'",
                Integer.class,
                "legacy_short_urls_to_links"
        );
        return count != null && count > 0;
    }

    private void recordAudit(String status, int migratedCount, String message) {
        jdbcTemplate.update(
                """
                INSERT INTO cutover_migration_audit (migration_name, status, migrated_count, message, created_at)
                VALUES (?, ?, ?, ?, now())
                """,
                "legacy_short_urls_to_links",
                status,
                migratedCount,
                message
        );
    }

    private record LegacyShortUrlRow(
            Long id,
            String originalUrl,
            String shortCode,
            java.time.LocalDateTime expiryAt,
            Integer clickCount,
            Integer maxClicks,
            Long userId,
            boolean active
    ) {}
}
