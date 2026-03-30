package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    List<ApiKey> findByWorkspaceId(Long workspaceId);

    Optional<ApiKey> findByWorkspaceIdAndKeyPrefix(Long workspaceId, String keyPrefix);

    List<ApiKey> findByWorkspaceIdAndRevokedAtIsNull(Long workspaceId);

    List<ApiKey> findByWorkspaceIdAndExpiresAtBefore(Long workspaceId, LocalDateTime expiresAt);
}
