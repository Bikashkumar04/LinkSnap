package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;
import com.bikash.LinkSnap.service.ShortUrlService;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.net.URI;

@RestController
@RequestMapping("/api/url")
public class ShortUrlController {

    private final ShortUrlService service;

    public ShortUrlController(ShortUrlService service) {
        this.service = service;
    }

    @PostMapping("/shorten")
    public ShortUrlResponseDTO shortenUrl(@RequestBody CreateShortUrlRequestDTO request) {
        return service.createShortUrl(request);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        String url = service.redirect(shortCode);

        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}