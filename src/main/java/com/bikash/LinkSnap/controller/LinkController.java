package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.LinkDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.LinkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;
    private final AuthorizationService authorizationService;

    public LinkController(LinkService linkService, AuthorizationService authorizationService) {
        this.linkService = linkService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<LinkDTO> createLink(
            @RequestBody LinkDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, request.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to create links");
        }
        return ResponseEntity.ok(linkService.createLink(request));
    }

    @PutMapping("/{linkId}")
    public ResponseEntity<LinkDTO> updateLink(
            @PathVariable Long linkId,
            @RequestBody LinkDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        LinkDTO existing = linkService.getLinkById(linkId);
        if (!authorizationService.canEditWorkspace(currentUserId, existing.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update this link");
        }
        return ResponseEntity.ok(linkService.updateLink(linkId, request));
    }

    @PatchMapping("/{linkId}/status")
    public ResponseEntity<LinkDTO> updateLinkStatus(
            @PathVariable Long linkId,
            @RequestParam boolean active,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        LinkDTO existing = linkService.getLinkById(linkId);
        if (!authorizationService.canEditWorkspace(currentUserId, existing.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to update this link");
        }
        return ResponseEntity.ok(linkService.pauseLink(linkId, active));
    }

    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteLink(
            @PathVariable Long linkId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        LinkDTO existing = linkService.getLinkById(linkId);
        if (!authorizationService.canEditWorkspace(currentUserId, existing.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete this link");
        }
        linkService.deleteLink(linkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{linkId}")
    public ResponseEntity<LinkDTO> getLink(
            @PathVariable Long linkId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        LinkDTO link = linkService.getLinkById(linkId);
        if (!authorizationService.canViewWorkspace(currentUserId, link.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this link");
        }
        return ResponseEntity.ok(link);
    }

    @GetMapping
    public ResponseEntity<List<LinkDTO>> listWorkspaceLinks(
            @RequestParam Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view links");
        }
        return ResponseEntity.ok(linkService.listWorkspaceLinks(workspaceId));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
