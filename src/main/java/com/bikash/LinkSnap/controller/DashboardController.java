package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.DashboardStatsResponse;
import com.bikash.LinkSnap.dto.TopLinkResponse;
import com.bikash.LinkSnap.services.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class DashboardController {
    private final UrlMappingService urlMappingService;
    @GetMapping("/dashboard/stats")
    public ResponseEntity<DashboardStatsResponse>
    getDashboardStats() {

        return ResponseEntity.ok(
                urlMappingService
                        .getDashboardStats()
        );
    }

    @GetMapping("/dashboard/top-links")
    public ResponseEntity<List<TopLinkResponse>>
    getTopLinks() {

        return ResponseEntity.ok(
                urlMappingService
                        .getTopLinks()
        );
    }
}
