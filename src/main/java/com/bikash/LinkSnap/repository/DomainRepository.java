package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.Domain;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DomainRepository extends JpaRepository<Domain, Long> {

    List<Domain> findByWorkspaceId(Long workspaceId);

    Optional<Domain> findByHost(String host);

    Optional<Domain> findByWorkspaceIdAndIsPrimaryTrue(Long workspaceId);

    boolean existsByHost(String host);
}
