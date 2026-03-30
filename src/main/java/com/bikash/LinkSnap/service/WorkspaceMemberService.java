package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.WorkspaceMemberDTO;

import java.util.List;

public interface WorkspaceMemberService {

    WorkspaceMemberDTO addMember(Long workspaceId, Long userId, String role);

    WorkspaceMemberDTO updateMemberRole(Long workspaceId, Long userId, String role);

    void removeMember(Long workspaceId, Long userId);

    List<WorkspaceMemberDTO> listWorkspaceMembers(Long workspaceId);
}
