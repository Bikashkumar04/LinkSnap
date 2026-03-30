package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    List<Tag> findByWorkspaceId(Long workspaceId);

    Optional<Tag> findByIdAndWorkspaceId(Long id, Long workspaceId);

    boolean existsByWorkspaceIdAndNameIgnoreCase(Long workspaceId, String name);
}
