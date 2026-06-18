package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.entity.ClickEvent;
import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.repository.ClickEventRepository;
import com.bikash.LinkSnap.repository.UrlMappingRepository;
import com.bikash.LinkSnap.services.UrlMappingService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlMappingRepository repository;
    private final ClickEventRepository clickEventRepository;
    private final UrlMappingService urlMappingService;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode,
            HttpServletRequest request) {

        UrlMapping urlMapping =
                repository.findByShortCode(shortCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "URL not found"));

        // Check if link is expired
        if (urlMapping.getExpiresAt() != null
                && LocalDateTime.now()
                .isAfter(
                        urlMapping.getExpiresAt()
                )) {

            return ResponseEntity
                    .status(HttpStatus.GONE)
                    .build();
        }

        // Increment click count
        urlMapping.setClickCount(
                urlMapping.getClickCount() + 1
        );

        repository.save(urlMapping);

        // Save click event
        ClickEvent clickEvent =
                new ClickEvent();

        clickEvent.setClickTime(
                LocalDateTime.now()
        );

        clickEvent.setIpAddress(
                request.getRemoteAddr()
        );

        clickEvent.setUserAgent(
                request.getHeader("User-Agent")
        );

        clickEvent.setUrlMapping(
                urlMapping
        );

        clickEventRepository.save(clickEvent);

        // Redirect
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(
                        URI.create(
                                urlMapping.getOriginalUrl()
                        )
                )
                .build();
    }


}