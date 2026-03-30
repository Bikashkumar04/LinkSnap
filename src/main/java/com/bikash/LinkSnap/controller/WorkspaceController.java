package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.WorkspaceDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.WorkspaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final AuthorizationService authorizationService;

    public WorkspaceController(WorkspaceService workspaceService, AuthorizationService authorizationService) {
        this.workspaceService = workspaceService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceDTO> createWorkspace(
            @RequestBody WorkspaceDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!currentUserId.equals(request.getOwnerUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot create workspace for another user");
        }
        return ResponseEntity.ok(workspaceService.createWorkspace(request));
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(
            @PathVariable Long workspaceId,
            @RequestBody WorkspaceDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to edit this workspace");
        }
        return ResponseEntity.ok(workspaceService.updateWorkspace(workspaceId, request));
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> getWorkspace(
            @PathVariable Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this workspace");
        }
        return ResponseEntity.ok(workspaceService.getWorkspaceById(workspaceId));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceDTO>> listUserWorkspaces(
            @RequestParam Long userId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!currentUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot list another user's workspaces");
        }
        return ResponseEntity.ok(workspaceService.listUserWorkspaces(userId));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
