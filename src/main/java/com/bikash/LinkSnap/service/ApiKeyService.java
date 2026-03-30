package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.ApiKeyDTO;

import java.util.List;

public interface ApiKeyService {

    ApiKeyDTO createApiKey(Long workspaceId, Long createdByUserId, ApiKeyDTO request);

    ApiKeyDTO rotateApiKey(Long workspaceId, Long apiKeyId);

    void revokeApiKey(Long workspaceId, Long apiKeyId);

    boolean validateApiKey(Long workspaceId, String rawApiKey);

    boolean hasRequiredScope(Long workspaceId, String rawApiKey, String requiredScope);

    void updateLastUsed(Long apiKeyId);

    List<ApiKeyDTO> listWorkspaceApiKeys(Long workspaceId);
}
