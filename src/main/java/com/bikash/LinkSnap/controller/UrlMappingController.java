package com.bikash.LinkSnap.controller;

import com.bikash.LinkSnap.dto.*;
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
    public ResponseEntity<List<LinkResponse>> getMyLinks() {

        return ResponseEntity.ok(
                urlMappingService.getMyLinks()
        );
    }

    //Get Single Link
    @GetMapping("/{id}")
    public ResponseEntity<LinkResponse> getLink(
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
    public ResponseEntity<LinkResponse> updateLink(
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

    @GetMapping("/{id}/click-history")
    public ResponseEntity<
            List<ClickEventResponse>>
    getClickHistory(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                urlMappingService
                        .getClickHistory(id)
        );
    }
}
