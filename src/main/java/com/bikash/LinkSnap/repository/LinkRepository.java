package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByDomainIdAndShortCode(Long domainId, String shortCode);

    boolean existsByDomainIdAndShortCode(Long domainId, String shortCode);

    Page<Link> findByWorkspaceIdAndDeletedAtIsNull(Long workspaceId, Pageable pageable);

    Page<Link> findByWorkspaceIdAndCreatedByUserIdAndDeletedAtIsNull(Long workspaceId, Long createdByUserId, Pageable pageable);

    Page<Link> findByWorkspaceIdAndIsActiveAndDeletedAtIsNull(Long workspaceId, boolean isActive, Pageable pageable);

    Page<Link> findByWorkspaceIdAndDeletedAtIsNullAndExpiresAtBefore(Long workspaceId, LocalDateTime expiresAt, Pageable pageable);
}
