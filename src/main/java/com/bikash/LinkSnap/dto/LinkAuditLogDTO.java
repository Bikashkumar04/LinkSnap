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
public class LinkAuditLogDTO {

    private Long id;
    private Long linkId;
    private Long actorUserId;
    private String action;
    private String beforeJson;
    private String afterJson;
    private LocalDateTime createdAt;
}
