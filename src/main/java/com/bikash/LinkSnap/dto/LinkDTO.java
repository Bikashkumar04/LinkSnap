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
public class LinkDTO {

    private Long id;
    private Long workspaceId;
    private Long domainId;
    private Long createdByUserId;
    private String shortCode;
    private String originalUrl;
    private String title;
    private boolean isActive;
    private LocalDateTime expiresAt;
    private Integer maxClicks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}
