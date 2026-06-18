package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class RedirectController {

    private final UrlMappingRepository repository;

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(
            @PathVariable String shortCode) {

        UrlMapping urlMapping = repository
                .findByShortCode(shortCode)
                .orElseThrow(() ->
                        new RuntimeException("URL not found"));

        urlMapping.setClickCount(
                urlMapping.getClickCount() + 1
        );

        repository.save(urlMapping);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(
                        URI.create(urlMapping.getOriginalUrl())
                )
                .build();
    }
}