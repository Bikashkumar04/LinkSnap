package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.WorkspaceMember;
import com.bikash.LinkSnap.entity.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {

    List<WorkspaceMember> findByIdWorkspaceId(Long workspaceId);

    List<WorkspaceMember> findByIdUserId(Long userId);

    boolean existsByIdWorkspaceIdAndIdUserId(Long workspaceId, Long userId);
}
