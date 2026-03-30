package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.TagDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}")
public class TagController {

    private final TagService tagService;
    private final AuthorizationService authorizationService;

    public TagController(TagService tagService, AuthorizationService authorizationService) {
        this.tagService = tagService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/tags")
    public ResponseEntity<TagDTO> createTag(
            @PathVariable Long workspaceId,
            @RequestBody TagDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to create tag");
        }
        return ResponseEntity.ok(tagService.createTag(workspaceId, request));
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagDTO>> listWorkspaceTags(
            @PathVariable Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view tags");
        }
        return ResponseEntity.ok(tagService.listWorkspaceTags(workspaceId));
    }

    @DeleteMapping("/tags/{tagId}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable Long workspaceId,
            @PathVariable Long tagId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to delete tag");
        }
        tagService.deleteTag(workspaceId, tagId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/links/{linkId}/tags/{tagId}")
    public ResponseEntity<TagDTO> assignTagToLink(
            @PathVariable Long workspaceId,
            @PathVariable Long linkId,
            @PathVariable Long tagId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to assign tags");
        }
        return ResponseEntity.ok(tagService.assignTagToLink(workspaceId, linkId, tagId));
    }

    @DeleteMapping("/links/{linkId}/tags/{tagId}")
    public ResponseEntity<Void> removeTagFromLink(
            @PathVariable Long workspaceId,
            @PathVariable Long linkId,
            @PathVariable Long tagId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to remove tags");
        }
        tagService.removeTagFromLink(workspaceId, linkId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/links/{linkId}/tags")
    public ResponseEntity<List<TagDTO>> listLinkTags(
            @PathVariable Long workspaceId,
            @PathVariable Long linkId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view tags");
        }
        return ResponseEntity.ok(tagService.listLinkTags(workspaceId, linkId));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
