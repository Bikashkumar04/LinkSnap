package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.LinkAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkAuditLogRepository extends JpaRepository<LinkAuditLog, Long> {

    Page<LinkAuditLog> findByLinkIdOrderByCreatedAtDesc(Long linkId, Pageable pageable);

    Page<LinkAuditLog> findByActorUserIdOrderByCreatedAtDesc(Long actorUserId, Pageable pageable);
}
