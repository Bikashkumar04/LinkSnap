package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.ApiKeyDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.ApiKeyService;
import com.bikash.LinkSnap.service.AuthorizationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/api-keys")
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final AuthorizationService authorizationService;

    public ApiKeyController(ApiKeyService apiKeyService, AuthorizationService authorizationService) {
        this.apiKeyService = apiKeyService;
        this.authorizationService = authorizationService;
    }

    @PostMapping
    public ResponseEntity<ApiKeyDTO> createApiKey(
            @PathVariable Long workspaceId,
            @RequestBody ApiKeyDTO request,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to create API keys");
        }
        return ResponseEntity.ok(apiKeyService.createApiKey(workspaceId, currentUserId, request));
    }

    @GetMapping
    public ResponseEntity<List<ApiKeyDTO>> listApiKeys(
            @PathVariable Long workspaceId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view API keys");
        }
        return ResponseEntity.ok(apiKeyService.listWorkspaceApiKeys(workspaceId));
    }

    @PostMapping("/{apiKeyId}/rotate")
    public ResponseEntity<ApiKeyDTO> rotateApiKey(
            @PathVariable Long workspaceId,
            @PathVariable Long apiKeyId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to rotate API keys");
        }
        return ResponseEntity.ok(apiKeyService.rotateApiKey(workspaceId, apiKeyId));
    }

    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> revokeApiKey(
            @PathVariable Long workspaceId,
            @PathVariable Long apiKeyId,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to revoke API keys");
        }
        apiKeyService.revokeApiKey(workspaceId, apiKeyId);
        return ResponseEntity.noContent().build();
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
