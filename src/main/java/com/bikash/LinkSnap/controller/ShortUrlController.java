package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;
import com.bikash.LinkSnap.service.RedirectService;
import com.bikash.LinkSnap.service.ShortUrlService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

@RestController
@Deprecated(since = "2026-03-30", forRemoval = false)
public class ShortUrlController {

    private static final Logger log = LoggerFactory.getLogger(ShortUrlController.class);
    private static final String SUNSET_DATE = "Wed, 31 Dec 2026 23:59:59 GMT";
    private static final String SUCCESSOR_LINK = "</api/links>; rel=\"successor-version\"";

    private final ShortUrlService service;
    private final RedirectService redirectService;

    public ShortUrlController(ShortUrlService service, RedirectService redirectService) {
        this.service = service;
        this.redirectService = redirectService;
    }

    // Create short URL
    @PostMapping("/api/urls/shorten")
    @Deprecated(since = "2026-03-30", forRemoval = false)
    public ResponseEntity<ShortUrlResponseDTO> shortenUrl(@RequestBody CreateShortUrlRequestDTO request) {
        ShortUrlResponseDTO response = service.createShortUrl(request);
        return ResponseEntity.ok()
                .header("Deprecation", "true")
                .header("Sunset", SUNSET_DATE)
                .header("Link", SUCCESSOR_LINK)
                .body(response);
    }

    // Redirect using short code
    @GetMapping("/{shortCode}")
    @Deprecated(since = "2026-03-30", forRemoval = false)
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
            log.warn("Legacy redirect fallback used for shortCode={}", shortCode);
            url = service.redirect(shortCode);
        }


        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header("Deprecation", "true")
                .header("Sunset", SUNSET_DATE)
                .header("Link", SUCCESSOR_LINK)
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
