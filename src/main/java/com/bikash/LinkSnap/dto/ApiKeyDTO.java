package com.bikash.LinkSnap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDTO {

    private Long id;
    private Long workspaceId;
    private Long createdByUserId;
    private String name;
    private String keyPrefix;
    private String rawApiKey;
    private String scopes;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private LocalDateTime createdAt;
}
