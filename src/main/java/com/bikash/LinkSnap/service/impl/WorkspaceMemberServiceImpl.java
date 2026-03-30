package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.WorkspaceMemberDTO;
import com.bikash.LinkSnap.entity.WorkspaceMember;
import com.bikash.LinkSnap.entity.WorkspaceMemberId;
import com.bikash.LinkSnap.repository.WorkspaceMemberRepository;
import com.bikash.LinkSnap.service.WorkspaceMemberService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WorkspaceMemberServiceImpl implements WorkspaceMemberService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    public WorkspaceMemberServiceImpl(WorkspaceMemberRepository workspaceMemberRepository) {
        this.workspaceMemberRepository = workspaceMemberRepository;
    }

    @Override
    @Transactional
    public WorkspaceMemberDTO addMember(Long workspaceId, Long userId, String role) {
        WorkspaceMember member = new WorkspaceMember();
        member.setId(new WorkspaceMemberId(workspaceId, userId));
        member.setRole(role == null ? "VIEWER" : role);
        member.setInvitedAt(LocalDateTime.now());
        member.setJoinedAt(LocalDateTime.now());
        return toDTO(workspaceMemberRepository.save(member));
    }

    @Override
    @Transactional
    public WorkspaceMemberDTO updateMemberRole(Long workspaceId, Long userId, String role) {
        WorkspaceMember member = workspaceMemberRepository.findById(new WorkspaceMemberId(workspaceId, userId))
                .orElseThrow(() -> new IllegalArgumentException("Workspace member not found"));
        member.setRole(role);
        return toDTO(workspaceMemberRepository.save(member));
    }

    @Override
    @Transactional
    public void removeMember(Long workspaceId, Long userId) {
        workspaceMemberRepository.deleteById(new WorkspaceMemberId(workspaceId, userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<WorkspaceMemberDTO> listWorkspaceMembers(Long workspaceId) {
        return workspaceMemberRepository.findByIdWorkspaceId(workspaceId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private WorkspaceMemberDTO toDTO(WorkspaceMember member) {
        return new WorkspaceMemberDTO(
                member.getId().getWorkspaceId(),
                member.getId().getUserId(),
                member.getRole(),
                member.getInvitedAt(),
                member.getJoinedAt()
        );
    }
}
