package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;
import com.bikash.LinkSnap.service.ShortUrlService;

import org.springframework.web.bind.annotation.*;

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
    public String redirect(@PathVariable String shortCode) {

        return service.redirect(shortCode);

    }
}