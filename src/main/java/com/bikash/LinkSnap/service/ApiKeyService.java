package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.ApiKeyDTO;

import java.util.List;

public interface ApiKeyService {

    ApiKeyDTO createApiKey(ApiKeyDTO request);

    ApiKeyDTO rotateApiKey(Long apiKeyId);

    void revokeApiKey(Long apiKeyId);

    boolean validateApiKey(Long workspaceId, String keyPrefix);

    void updateLastUsed(Long apiKeyId);

    List<ApiKeyDTO> listWorkspaceApiKeys(Long workspaceId);
}
