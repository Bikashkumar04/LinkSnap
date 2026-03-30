package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.entity.Workspace;
import com.bikash.LinkSnap.entity.WorkspaceMember;
import com.bikash.LinkSnap.entity.WorkspaceMemberId;
import com.bikash.LinkSnap.repository.WorkspaceMemberRepository;
import com.bikash.LinkSnap.repository.WorkspaceRepository;
import com.bikash.LinkSnap.service.AuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public AuthorizationServiceImpl(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canViewWorkspace(Long userId, Long workspaceId) {
        return isWorkspaceOwner(userId, workspaceId) ||
                workspaceMemberRepository.existsByIdWorkspaceIdAndIdUserId(workspaceId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canEditWorkspace(Long userId, Long workspaceId) {
        if (isWorkspaceOwner(userId, workspaceId)) {
            return true;
        }
        WorkspaceMember member = workspaceMemberRepository.findById(new WorkspaceMemberId(workspaceId, userId))
                .orElse(null);
        if (member == null) {
            return false;
        }
        return "OWNER".equalsIgnoreCase(member.getRole()) || "EDITOR".equalsIgnoreCase(member.getRole());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isWorkspaceOwner(Long userId, Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId).orElse(null);
        return workspace != null && workspace.getOwnerUserId().equals(userId);
    }
}
