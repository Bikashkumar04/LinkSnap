package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.DomainDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.DomainService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
public class DomainController {

    private final DomainService domainService;
    private final AuthorizationService authorizationService;

    public DomainController(DomainService domainService, AuthorizationService authorizationService) {
        this.domainService = domainService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<DomainDTO> addDomain(
            @RequestBody DomainDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, request.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to add domain");
        }
        return ResponseEntity.ok(domainService.addDomain(request));
    }

    @PostMapping("/{domainId}/verify")
    public ResponseEntity<DomainDTO> verifyDomain(
            @PathVariable Long domainId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        DomainDTO domain = domainService.getDomainById(domainId);
        if (!authorizationService.canEditWorkspace(currentUserId, domain.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to verify domain");
        }
        return ResponseEntity.ok(domainService.verifyDomain(domainId));
    }

    @PostMapping("/{domainId}/primary")
    public ResponseEntity<DomainDTO> setPrimaryDomain(
            @PathVariable Long domainId,
            @RequestParam Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to change primary domain");
        }
        return ResponseEntity.ok(domainService.setPrimaryDomain(workspaceId, domainId));
    }

    @GetMapping
    public ResponseEntity<List<DomainDTO>> listWorkspaceDomains(
            @RequestParam Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view domains");
        }
        return ResponseEntity.ok(domainService.listWorkspaceDomains(workspaceId));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
