package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {

    Optional<Link> findByDomainIdAndShortCode(Long domainId, String shortCode);

    boolean existsByDomainIdAndShortCode(Long domainId, String shortCode);

    Page<Link> findByWorkspaceIdAndDeletedAtIsNull(Long workspaceId, Pageable pageable);

    Page<Link> findByWorkspaceIdAndCreatedByUserIdAndDeletedAtIsNull(Long workspaceId, Long createdByUserId, Pageable pageable);

    Page<Link> findByWorkspaceIdAndIsActiveAndDeletedAtIsNull(Long workspaceId, boolean isActive, Pageable pageable);

    Page<Link> findByWorkspaceIdAndDeletedAtIsNullAndExpiresAtBefore(Long workspaceId, LocalDateTime expiresAt, Pageable pageable);

    @Query(
            value = """
                    SELECT l.* FROM links l
                    LEFT JOIN link_tag_map ltm ON l.id = ltm.link_id
                    WHERE l.workspace_id = :workspaceId
                      AND l.deleted_at IS NULL
                      AND (:createdByUserId IS NULL OR l.created_by_user_id = :createdByUserId)
                      AND (:active IS NULL OR l.is_active = :active)
                      AND (:tagId IS NULL OR ltm.tag_id = :tagId)
                      AND (:search IS NULL
                           OR LOWER(l.original_url) LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(COALESCE(l.title, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(l.short_code) LIKE LOWER(CONCAT('%', :search, '%')))
                    GROUP BY l.id
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT l.id) FROM links l
                    LEFT JOIN link_tag_map ltm ON l.id = ltm.link_id
                    WHERE l.workspace_id = :workspaceId
                      AND l.deleted_at IS NULL
                      AND (:createdByUserId IS NULL OR l.created_by_user_id = :createdByUserId)
                      AND (:active IS NULL OR l.is_active = :active)
                      AND (:tagId IS NULL OR ltm.tag_id = :tagId)
                      AND (:search IS NULL
                           OR LOWER(l.original_url) LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(COALESCE(l.title, '')) LIKE LOWER(CONCAT('%', :search, '%'))
                           OR LOWER(l.short_code) LIKE LOWER(CONCAT('%', :search, '%')))
                    """,
            nativeQuery = true
    )
    Page<Link> searchDashboardLinks(
            @Param("workspaceId") Long workspaceId,
            @Param("createdByUserId") Long createdByUserId,
            @Param("active") Boolean active,
            @Param("tagId") Long tagId,
            @Param("search") String search,
            Pageable pageable
    );
}
