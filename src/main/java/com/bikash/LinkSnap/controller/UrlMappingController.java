package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.UrlMappingDto;
import com.bikash.LinkSnap.services.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/url-mapping")
@AllArgsConstructor
public class UrlMappingController {
    private final UrlMappingService urlMappingService;

    @PostMapping("/shorten")
    public ResponseEntity<UrlMappingDto> shortenUrl(@RequestBody UrlMappingDto urlMappingDto) {
        // Implement the logic to shorten the URL using urlMappingService
        return ResponseEntity.ok(
                urlMappingService.shortenUrl(urlMappingDto)
        );
    }
}
