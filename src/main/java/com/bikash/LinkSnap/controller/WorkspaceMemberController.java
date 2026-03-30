package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.WorkspaceMemberDTO;
import com.bikash.LinkSnap.service.WorkspaceMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/members")
public class WorkspaceMemberController {

    private final WorkspaceMemberService workspaceMemberService;

    public WorkspaceMemberController(WorkspaceMemberService workspaceMemberService) {
        this.workspaceMemberService = workspaceMemberService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceMemberDTO> addMember(
            @PathVariable Long workspaceId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "VIEWER") String role
    ) {
        return ResponseEntity.ok(workspaceMemberService.addMember(workspaceId, userId, role));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<WorkspaceMemberDTO> updateMemberRole(
            @PathVariable Long workspaceId,
            @PathVariable Long userId,
            @RequestParam String role
    ) {
        return ResponseEntity.ok(workspaceMemberService.updateMemberRole(workspaceId, userId, role));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long workspaceId,
            @PathVariable Long userId
    ) {
        workspaceMemberService.removeMember(workspaceId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceMemberDTO>> listWorkspaceMembers(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(workspaceMemberService.listWorkspaceMembers(workspaceId));
    }
}
