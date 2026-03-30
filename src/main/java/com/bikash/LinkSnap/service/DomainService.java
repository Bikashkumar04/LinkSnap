package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.DomainDTO;

import java.util.List;

public interface DomainService {

    DomainDTO addDomain(DomainDTO request);

    DomainDTO verifyDomain(Long domainId);

    DomainDTO setPrimaryDomain(Long workspaceId, Long domainId);

    List<DomainDTO> listWorkspaceDomains(Long workspaceId);
}
