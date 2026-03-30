package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.LinkDTO;
import com.bikash.LinkSnap.service.LinkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/links")
public class LinkController {

    private final LinkService linkService;

    public LinkController(LinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<LinkDTO> createLink(@RequestBody LinkDTO request) {
        return ResponseEntity.ok(linkService.createLink(request));
    }

    @PutMapping("/{linkId}")
    public ResponseEntity<LinkDTO> updateLink(
            @PathVariable Long linkId,
            @RequestBody LinkDTO request
    ) {
        return ResponseEntity.ok(linkService.updateLink(linkId, request));
    }

    @PatchMapping("/{linkId}/status")
    public ResponseEntity<LinkDTO> updateLinkStatus(
            @PathVariable Long linkId,
            @RequestParam boolean active
    ) {
        return ResponseEntity.ok(linkService.pauseLink(linkId, active));
    }

    @DeleteMapping("/{linkId}")
    public ResponseEntity<Void> deleteLink(@PathVariable Long linkId) {
        linkService.deleteLink(linkId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{linkId}")
    public ResponseEntity<LinkDTO> getLink(@PathVariable Long linkId) {
        return ResponseEntity.ok(linkService.getLinkById(linkId));
    }

    @GetMapping
    public ResponseEntity<List<LinkDTO>> listWorkspaceLinks(@RequestParam Long workspaceId) {
        return ResponseEntity.ok(linkService.listWorkspaceLinks(workspaceId));
    }
}
