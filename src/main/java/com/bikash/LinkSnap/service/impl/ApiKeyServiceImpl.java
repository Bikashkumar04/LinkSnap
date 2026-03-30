package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.ApiKeyDTO;
import com.bikash.LinkSnap.entity.ApiKey;
import com.bikash.LinkSnap.repository.ApiKeyRepository;
import com.bikash.LinkSnap.service.ApiKeyService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyRepository apiKeyRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public ApiKeyServiceImpl(ApiKeyRepository apiKeyRepository, PasswordEncoder passwordEncoder) {
        this.apiKeyRepository = apiKeyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public ApiKeyDTO createApiKey(Long workspaceId, Long createdByUserId, ApiKeyDTO request) {
        GeneratedApiKey generated = generateRawApiKey();

        ApiKey apiKey = new ApiKey();
        apiKey.setWorkspaceId(workspaceId);
        apiKey.setCreatedByUserId(createdByUserId);
        apiKey.setName(request.getName());
        apiKey.setScopes(request.getScopes() == null ? "links:read" : request.getScopes());
        apiKey.setKeyPrefix(generated.prefix());
        apiKey.setKeyHash(passwordEncoder.encode(generated.raw()));
        apiKey.setExpiresAt(request.getExpiresAt());

        ApiKeyDTO response = toDTO(apiKeyRepository.save(apiKey));
        response.setRawApiKey(generated.raw());
        return response;
    }

    @Override
    @Transactional
    public ApiKeyDTO rotateApiKey(Long workspaceId, Long apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findByIdAndWorkspaceId(apiKeyId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));

        GeneratedApiKey generated = generateRawApiKey();
        apiKey.setKeyPrefix(generated.prefix());
        apiKey.setKeyHash(passwordEncoder.encode(generated.raw()));
        apiKey.setRevokedAt(null);

        ApiKeyDTO response = toDTO(apiKeyRepository.save(apiKey));
        response.setRawApiKey(generated.raw());
        return response;
    }

    @Override
    @Transactional
    public void revokeApiKey(Long workspaceId, Long apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findByIdAndWorkspaceId(apiKeyId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("API key not found"));
        apiKey.setRevokedAt(LocalDateTime.now());
        apiKeyRepository.save(apiKey);
    }

    @Override
    @Transactional
    public boolean validateApiKey(Long workspaceId, String rawApiKey) {
        String keyPrefix = extractPrefix(rawApiKey);
        if (keyPrefix == null) {
            return false;
        }

        return apiKeyRepository.findByWorkspaceIdAndKeyPrefix(workspaceId, keyPrefix)
                .filter(key -> key.getRevokedAt() == null)
                .filter(key -> key.getExpiresAt() == null || key.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(key -> passwordEncoder.matches(rawApiKey, key.getKeyHash()))
                .map(key -> {
                    key.setLastUsedAt(LocalDateTime.now());
                    apiKeyRepository.save(key);
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRequiredScope(Long workspaceId, String rawApiKey, String requiredScope) {
        String keyPrefix = extractPrefix(rawApiKey);
        if (keyPrefix == null || requiredScope == null || requiredScope.isBlank()) {
            return false;
        }
        return apiKeyRepository.findByWorkspaceIdAndKeyPrefix(workspaceId, keyPrefix)
                .filter(key -> key.getRevokedAt() == null)
                .filter(key -> key.getExpiresAt() == null || key.getExpiresAt().isAfter(LocalDateTime.now()))
                .filter(key -> passwordEncoder.matches(rawApiKey, key.getKeyHash()))
                .map(key -> parseScopes(key.getScopes()).contains(requiredScope))
                .orElse(false);
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

    private ApiKeyDTO toDTO(ApiKey apiKey) {
        return new ApiKeyDTO(
                apiKey.getId(),
                apiKey.getWorkspaceId(),
                apiKey.getCreatedByUserId(),
                apiKey.getName(),
                apiKey.getKeyPrefix(),
                null,
                apiKey.getScopes(),
                apiKey.getLastUsedAt(),
                apiKey.getExpiresAt(),
                apiKey.getRevokedAt(),
                apiKey.getCreatedAt()
        );
    }

    private GeneratedApiKey generateRawApiKey() {
        String prefix;
        do {
            prefix = "ls_" + randomAlphaNumeric(10);
        } while (apiKeyRepository.existsByKeyPrefix(prefix));

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        String secret = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        String raw = prefix + "." + secret;

        return new GeneratedApiKey(prefix, raw);
    }

    private String randomAlphaNumeric(int length) {
        final String chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String extractPrefix(String rawApiKey) {
        if (rawApiKey == null || rawApiKey.isBlank()) {
            return null;
        }
        int dotIndex = rawApiKey.indexOf('.');
        if (dotIndex <= 0) {
            return null;
        }
        return rawApiKey.substring(0, dotIndex);
    }

    private Set<String> parseScopes(String scopes) {
        if (scopes == null || scopes.isBlank()) {
            return Set.of();
        }
        return java.util.Arrays.stream(scopes.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }

    private record GeneratedApiKey(String prefix, String raw) {}
}
