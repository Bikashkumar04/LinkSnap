package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.WorkspaceDTO;
import com.bikash.LinkSnap.service.WorkspaceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces")
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    public WorkspaceController(WorkspaceService workspaceService) {
        this.workspaceService = workspaceService;
    }

    @PostMapping
    public ResponseEntity<WorkspaceDTO> createWorkspace(@RequestBody WorkspaceDTO request) {
        return ResponseEntity.ok(workspaceService.createWorkspace(request));
    }

    @PutMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> updateWorkspace(
            @PathVariable Long workspaceId,
            @RequestBody WorkspaceDTO request
    ) {
        return ResponseEntity.ok(workspaceService.updateWorkspace(workspaceId, request));
    }

    @GetMapping("/{workspaceId}")
    public ResponseEntity<WorkspaceDTO> getWorkspace(@PathVariable Long workspaceId) {
        return ResponseEntity.ok(workspaceService.getWorkspaceById(workspaceId));
    }

    @GetMapping
    public ResponseEntity<List<WorkspaceDTO>> listUserWorkspaces(@RequestParam Long userId) {
        return ResponseEntity.ok(workspaceService.listUserWorkspaces(userId));
    }
}
