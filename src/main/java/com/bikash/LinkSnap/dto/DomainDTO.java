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
public class DomainDTO {

    private Long id;
    private Long workspaceId;
    private String host;
    private boolean isPrimary;
    private String verificationStatus;
    private LocalDateTime createdAt;
}
