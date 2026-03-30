package com.bikash.LinkSnap.service;

public interface AuthorizationService {

    boolean canViewWorkspace(Long userId, Long workspaceId);

    boolean canEditWorkspace(Long userId, Long workspaceId);

    boolean isWorkspaceOwner(Long userId, Long workspaceId);
}
