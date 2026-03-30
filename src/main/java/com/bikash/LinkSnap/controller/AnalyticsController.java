package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.LinkDTO;
import com.bikash.LinkSnap.dto.LinkDailyStatsDTO;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.service.AuthorizationService;
import com.bikash.LinkSnap.service.LinkDailyStatsService;
import com.bikash.LinkSnap.service.LinkService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workspaces/{workspaceId}/analytics")
public class AnalyticsController {

    private final LinkDailyStatsService linkDailyStatsService;
    private final LinkService linkService;
    private final AuthorizationService authorizationService;

    public AnalyticsController(
            LinkDailyStatsService linkDailyStatsService,
            LinkService linkService,
            AuthorizationService authorizationService
    ) {
        this.linkDailyStatsService = linkDailyStatsService;
        this.linkService = linkService;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/aggregate")
    public ResponseEntity<Void> aggregateDailyStats(
            @PathVariable Long workspaceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canEditWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to aggregate stats");
        }
        linkDailyStatsService.aggregateForDate(date);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/links/{linkId}/daily")
    public ResponseEntity<List<LinkDailyStatsDTO>> getLinkDailyStats(
            @PathVariable Long workspaceId,
            @PathVariable Long linkId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication authentication
    ) {
        Long currentUserId = currentUserId(authentication);
        if (!authorizationService.canViewWorkspace(currentUserId, workspaceId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to view stats");
        }

        LinkDTO link = linkService.getLinkById(linkId);
        if (!workspaceId.equals(link.getWorkspaceId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Link does not belong to workspace");
        }

        return ResponseEntity.ok(linkDailyStatsService.getLinkStats(linkId, from, to));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        return user.getId();
    }
}
