package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.LinkDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.LinkService;
import com.bikash.LinkSnap.service.QrCodeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    private final QrCodeService qrCodeService;

    public LinkController(
            LinkService linkService,
            AuthorizationService authorizationService,
            QrCodeService qrCodeService
    ) {
        this.linkService = linkService;
        this.authorizationService = authorizationService;
        this.qrCodeService = qrCodeService;
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

    @GetMapping(value = "/{linkId}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getLinkQrCode(
            @PathVariable Long linkId,
            Authentication authentication,
            @RequestParam(defaultValue = "300") int size
    ) {
        Long currentUserId = currentUserId(authentication);
        LinkDTO link = linkService.getLinkById(linkId);
        if (!authorizationService.canViewWorkspace(currentUserId, link.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view this link");
        }

        int qrSize = Math.max(120, Math.min(size, 1000));
        String qrContent = linkService.buildShortLink(linkId);
        byte[] png = qrCodeService.generatePng(qrContent, qrSize);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(png);
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
