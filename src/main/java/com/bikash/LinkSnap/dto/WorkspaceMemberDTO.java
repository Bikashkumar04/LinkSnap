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
public class WorkspaceMemberDTO {

    private Long workspaceId;
    private Long userId;
    private String role;
    private LocalDateTime invitedAt;
    private LocalDateTime joinedAt;
}
