package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.DomainDTO;
import com.bikash.LinkSnap.service.DomainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/domains")
public class DomainController {

    private final DomainService domainService;

    public DomainController(DomainService domainService) {
        this.domainService = domainService;
    }

    @PostMapping
    public ResponseEntity<DomainDTO> addDomain(@RequestBody DomainDTO request) {
        return ResponseEntity.ok(domainService.addDomain(request));
    }

    @PostMapping("/{domainId}/verify")
    public ResponseEntity<DomainDTO> verifyDomain(@PathVariable Long domainId) {
        return ResponseEntity.ok(domainService.verifyDomain(domainId));
    }

    @PostMapping("/{domainId}/primary")
    public ResponseEntity<DomainDTO> setPrimaryDomain(
            @PathVariable Long domainId,
            @RequestParam Long workspaceId
    ) {
        return ResponseEntity.ok(domainService.setPrimaryDomain(workspaceId, domainId));
    }

    @GetMapping
    public ResponseEntity<List<DomainDTO>> listWorkspaceDomains(@RequestParam Long workspaceId) {
        return ResponseEntity.ok(domainService.listWorkspaceDomains(workspaceId));
    }
}
