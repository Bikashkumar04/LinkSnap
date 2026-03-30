package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.service.RedirectService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;

@RestController
public class RedirectController {

    private final RedirectService redirectService;

    public RedirectController(RedirectService redirectService) {
        this.redirectService = redirectService;
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams
    ) {
        String url = redirectService.resolveRedirectUrl(
                normalizeHost(request.getHeader("Host")),
                shortCode,
                extractClientIp(request),
                request.getHeader("Referer"),
                request.getHeader("User-Agent"),
                queryParams.get("utm_source"),
                queryParams.get("utm_medium"),
                queryParams.get("utm_campaign")
        );

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }

    private String normalizeHost(String hostHeader) {
        if (hostHeader == null || hostHeader.isBlank()) {
            return "";
        }
        int colonIndex = hostHeader.indexOf(':');
        return colonIndex > 0 ? hostHeader.substring(0, colonIndex) : hostHeader;
    }

    private String extractClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int commaIndex = forwardedFor.indexOf(',');
            return (commaIndex > 0 ? forwardedFor.substring(0, commaIndex) : forwardedFor).trim();
        }
        return request.getRemoteAddr();
    }
}
