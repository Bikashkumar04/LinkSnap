package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.WorkspaceDTO;

import java.util.List;

public interface WorkspaceService {

    WorkspaceDTO createWorkspace(WorkspaceDTO request);

    WorkspaceDTO updateWorkspace(Long workspaceId, WorkspaceDTO request);

    List<WorkspaceDTO> listUserWorkspaces(Long userId);

    WorkspaceDTO getWorkspaceById(Long workspaceId);
}
