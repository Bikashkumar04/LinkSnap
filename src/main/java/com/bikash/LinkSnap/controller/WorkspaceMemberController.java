package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.WorkspaceMemberDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.WorkspaceMemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/members")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;
    private final AuthorizationService authorizationService;

    public WorkspaceMemberController(
            WorkspaceMemberService workspaceMemberService,
            AuthorizationService authorizationService
    ) {
        this.workspaceMemberService = workspaceMemberService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceMemberDTO> addMember(
            @PathVariable Long workspaceId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "VIEWER") String role,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to add members");
        }
        return ResponseEntity.ok(workspaceMemberService.addMember(workspaceId, userId, role));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<WorkspaceMemberDTO> updateMemberRole(
            @PathVariable Long workspaceId,
            @PathVariable Long userId,
            @RequestParam String role,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update members");
        }
        return ResponseEntity.ok(workspaceMemberService.updateMemberRole(workspaceId, userId, role));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long workspaceId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to remove members");
        }
        workspaceMemberService.removeMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceMemberDTO>> listWorkspaceMembers(
            @PathVariable Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view members");
        }
        return ResponseEntity.ok(workspaceMemberService.listWorkspaceMembers(workspaceId));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
