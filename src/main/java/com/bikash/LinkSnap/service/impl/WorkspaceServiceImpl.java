package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.WorkspaceDTO;
import com.bikash.LinkSnap.entity.Workspace;
import com.bikash.LinkSnap.entity.WorkspaceMember;
import com.bikash.LinkSnap.repository.WorkspaceMemberRepository;
import com.bikash.LinkSnap.repository.WorkspaceRepository;
import com.bikash.LinkSnap.service.WorkspaceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceServiceImpl(
            WorkspaceRepository workspaceRepository,
            WorkspaceMemberRepository workspaceMemberRepository
    ) {
        this.workspaceRepository = workspaceRepository;
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    @Transactional
    public WorkspaceDTO createWorkspace(WorkspaceDTO request) {
        Workspace workspace = new Workspace();
        workspace.setName(request.getName());
        workspace.setSlug(request.getSlug());
        workspace.setOwnerUserId(request.getOwnerUserId());
        workspace.setPlanTier(request.getPlanTier() == null ? "FREE" : request.getPlanTier());
        return toDTO(workspaceRepository.save(workspace));
    }

    @Override
    @Transactional
    public WorkspaceDTO updateWorkspace(Long workspaceId, WorkspaceDTO request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        if (request.getName() != null) {
            workspace.setName(request.getName());
        }
        if (request.getPlanTier() != null) {
            workspace.setPlanTier(request.getPlanTier());
        }

        return toDTO(workspaceRepository.save(workspace));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceDTO> listUserWorkspaces(Long userId) {
        List<WorkspaceMember> memberships = workspaceMemberRepository.findByIdUserId(userId);
        List<Long> workspaceIds = memberships.stream()
                .map(member -> member.getId().getWorkspaceId())
                .toList();
        return workspaceRepository.findAllById(workspaceIds).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public WorkspaceDTO getWorkspaceById(Long workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        return toDTO(workspace);
    }

    private WorkspaceDTO toDTO(Workspace workspace) {
        return new WorkspaceDTO(
                workspace.getId(),
                workspace.getName(),
                workspace.getSlug(),
                workspace.getOwnerUserId(),
                workspace.getPlanTier(),
                workspace.getCreatedAt(),
                workspace.getUpdatedAt()
        );
    }
}
