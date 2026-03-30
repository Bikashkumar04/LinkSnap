package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.ApiKeyDTO;
import com.bikash.LinkSnap.entity.ApiKey;
import com.bikash.LinkSnap.repository.ApiKeyRepository;
import com.bikash.LinkSnap.service.ApiKeyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @Override
    @Transactional
    public ApiKeyDTO createApiKey(ApiKeyDTO request) {
        ApiKey apiKey = new ApiKey();
        apiKey.setWorkspaceId(request.getWorkspaceId());
        apiKey.setCreatedByUserId(request.getCreatedByUserId());
        apiKey.setName(request.getName());
        apiKey.setScopes(request.getScopes() == null ? "links:read" : request.getScopes());
        apiKey.setKeyPrefix(generatePrefix());
        apiKey.setKeyHash("TODO_HASH");
        apiKey.setExpiresAt(request.getExpiresAt());
        return toDTO(apiKeyRepository.save(apiKey));
    }

    @Override
    @Transactional
    public ApiKeyDTO rotateApiKey(Long apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));
        apiKey.setKeyPrefix(generatePrefix());
        apiKey.setKeyHash("TODO_HASH");
        apiKey.setRevokedAt(null);
        return toDTO(apiKeyRepository.save(apiKey));
    }

    @Override
    @Transactional
    public void revokeApiKey(Long apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));
        apiKey.setRevokedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateApiKey(Long workspaceId, String keyPrefix) {
        return apiKeyRepository.findByWorkspaceIdAndKeyPrefix(workspaceId, keyPrefix)
                .filter(key -> key.getRevokedAt() == null)
                .filter(key -> key.getExpiresAt() == null || key.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    @Override
    @Transactional
    public void updateLastUsed(Long apiKeyId) {
        apiKeyRepository.findById(apiKeyId).ifPresent(apiKey -> {
            apiKey.setLastUsedAt(LocalDateTime.now());
            apiKeyRepository.save(apiKey);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApiKeyDTO> listWorkspaceApiKeys(Long workspaceId) {
        return apiKeyRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private String generatePrefix() {
        return "ls_" + UUID.randomUUID().toString().replace("-", "").substring(0, 10);
    }

    private ApiKeyDTO toDTO(ApiKey apiKey) {
        return new ApiKeyDTO(
                apiKey.getId(),
                apiKey.getWorkspaceId(),
                apiKey.getCreatedByUserId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                apiKey.getScopes(),
                apiKey.getLastUsedAt(),
                apiKey.getExpiresAt(),
                apiKey.getRevokedAt(),
                apiKey.getCreatedAt()
        );
    }
}
