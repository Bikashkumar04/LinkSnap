package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;
import com.bikash.LinkSnap.service.RedirectService;
import com.bikash.LinkSnap.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Map;

@RestController
public class ShortUrlController {

    private final ShortUrlService service;
    private final RedirectService redirectService;

    public ShortUrlController(ShortUrlService service, RedirectService redirectService) {
        this.service = service;
        this.redirectService = redirectService;
    }

    // Create short URL
    @PostMapping("/api/urls/shorten")
    public ShortUrlResponseDTO shortenUrl(@RequestBody CreateShortUrlRequestDTO request) {
        return service.createShortUrl(request);
    }

    // Redirect using short code
    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request,
            @RequestParam Map<String, String> queryParams
    ) {

        String url;
        try {
            url = redirectService.resolveRedirectUrl(
                    normalizeHost(request.getHeader("Host")),
                    shortCode,
                    extractClientIp(request),
                    request.getHeader("Referer"),
                    request.getHeader("User-Agent"),
                    queryParams.get("utm_source"),
                    queryParams.get("utm_medium"),
                    queryParams.get("utm_campaign")
            );
        } catch (IllegalArgumentException ex) {
            // Backward compatibility while legacy short_urls is still active.
            url = service.redirect(shortCode);
        }


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
