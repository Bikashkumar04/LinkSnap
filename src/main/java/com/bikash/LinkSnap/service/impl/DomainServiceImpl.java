package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.DomainDTO;
import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.service.DomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DomainServiceImpl implements DomainService {

    private final DomainRepository domainRepository;

    public DomainServiceImpl(DomainRepository domainRepository) {
        this.domainRepository = domainRepository;
    }

    @Override
    @Transactional
    public DomainDTO addDomain(DomainDTO request) {
        if (domainRepository.existsByHost(request.getHost())) {
            throw new IllegalArgumentException("Domain already exists");
        }
        Domain domain = new Domain();
        domain.setWorkspaceId(request.getWorkspaceId());
        domain.setHost(request.getHost());
        domain.setPrimary(request.isPrimary());
        domain.setVerificationStatus(request.getVerificationStatus() == null ? "PENDING" : request.getVerificationStatus());
        return toDTO(domainRepository.save(domain));
    }

    @Override
    @Transactional
    public DomainDTO verifyDomain(Long domainId) {
        Domain domain = domainRepository.findById(domainId)
                .orElseThrow(() -> new IllegalArgumentException("Domain not found"));
        domain.setVerificationStatus("VERIFIED");
        return toDTO(domainRepository.save(domain));
    }

    @Override
    @Transactional
    public DomainDTO setPrimaryDomain(Long workspaceId, Long domainId) {
        List<Domain> workspaceDomains = domainRepository.findByWorkspaceId(workspaceId);
        Domain target = null;
        for (Domain domain : workspaceDomains) {
            boolean isTarget = domain.getId().equals(domainId);
            domain.setPrimary(isTarget);
            if (isTarget) {
                target = domain;
            }
        }
        domainRepository.saveAll(workspaceDomains);
        if (target == null) {
            throw new IllegalArgumentException("Domain not found in workspace");
        }
        return toDTO(target);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainDTO> listWorkspaceDomains(Long workspaceId) {
        return domainRepository.findByWorkspaceId(workspaceId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    private DomainDTO toDTO(Domain domain) {
        return new DomainDTO(
                domain.getId(),
                domain.getWorkspaceId(),
                domain.getHost(),
                domain.isPrimary(),
                domain.getVerificationStatus(),
                domain.getCreatedAt()
        );
    }
}
