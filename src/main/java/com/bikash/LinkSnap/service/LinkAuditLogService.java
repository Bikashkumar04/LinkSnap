package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.LinkAuditLogDTO;

import java.util.List;

public interface LinkAuditLogService {

    LinkAuditLogDTO createAuditLog(LinkAuditLogDTO request);

    List<LinkAuditLogDTO> listByLinkId(Long linkId);
}
