package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.UpdateUrlRequest;
import com.bikash.LinkSnap.dto.UrlAnalyticsResponse;
import com.bikash.LinkSnap.dto.UrlMappingDto;
import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.services.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/my-links")
    public ResponseEntity<List<UrlMapping>> getMyLinks() {

        return ResponseEntity.ok(
                urlMappingService.getMyLinks()
        );
    }

    //Get Single Link
    @GetMapping("/{id}")
    public ResponseEntity<UrlMapping> getLink(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                urlMappingService.getLink(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLink(
            @PathVariable Long id) {

        urlMappingService.deleteLink(id);

        return ResponseEntity.ok(
                "Link deleted successfully"
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<UrlMapping> updateLink(
            @PathVariable Long id,
            @RequestBody UpdateUrlRequest request) {

        return ResponseEntity.ok(
                urlMappingService.updateLink(
                        id,
                        request
                )
        );
    }


    @GetMapping("/{id}/analytics")
    public ResponseEntity<UrlAnalyticsResponse>
    getAnalytics(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                urlMappingService
                        .getAnalytics(id)
        );
    }
}
