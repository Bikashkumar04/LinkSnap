package com.bikash.LinkSnap.services;

import com.bikash.LinkSnap.dto.DashboardStatsResponse;
import com.bikash.LinkSnap.dto.UpdateUrlRequest;
import com.bikash.LinkSnap.dto.UrlAnalyticsResponse;
import com.bikash.LinkSnap.dto.UrlMappingDto;
import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.repository.UrlMappingRepository;
import com.bikash.LinkSnap.security.AuthenticationService;
import com.bikash.LinkSnap.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository repository;
    private final Base62Encoder base62Encoder;
    private final AuthenticationService authenticationService;

    public UrlMappingDto shortenUrl(UrlMappingDto dto) {

        // Get the currently authenticated user it
        User currentUser = authenticationService.getCurrentUser();


        UrlMapping urlMapping = new UrlMapping();

        urlMapping.setOriginalUrl(dto.getOriginalUrl());
        urlMapping.setUser(currentUser);

        UrlMapping saved = repository.save(urlMapping);

        String shortCode =
                base62Encoder.encode(saved.getId());

        saved.setShortCode(shortCode);

        repository.save(saved);

        UrlMappingDto response = new UrlMappingDto();

        response.setOriginalUrl(saved.getOriginalUrl());
        response.setShortCode(saved.getShortCode());
        response.setShortUrl(
                "http://localhost:8080/" + saved.getShortCode()
        );

        return response;
    }


    // this method will return all the URL mappings created by the currently authenticated user
    // for example id	username
    //1	bikash
    //2	john
    public List<UrlMapping> getMyLinks() {

        User currentUser =
                authenticationService.getCurrentUser();

        return repository.findByUser(currentUser);
    }


    //Get Single Link
    public UrlMapping getLink(Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        return repository
                .findByIdAndUser(id, currentUser)
                .orElseThrow(() ->
                        new RuntimeException("Link not found"));
    }


    //delete shortLink

    public void deleteLink(Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping urlMapping =
                repository.findByIdAndUser(
                        id,
                        currentUser
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Link not found"));

        repository.delete(urlMapping);
    }


    //update shortLink
    public UrlMapping updateLink(
            Long id,
            UpdateUrlRequest request) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping urlMapping =
                repository.findByIdAndUser(
                        id,
                        currentUser
                ).orElseThrow(() ->
                        new RuntimeException(
                                "Link not found"));

        urlMapping.setOriginalUrl(
                request.getOriginalUrl()
        );

        return repository.save(urlMapping);
    }


    public UrlAnalyticsResponse getAnalytics(
            Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping urlMapping =
                repository.findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "URL not found"));

        return UrlAnalyticsResponse
                .builder()
                .originalUrl(
                        urlMapping.getOriginalUrl()
                )
                .shortCode(
                        urlMapping.getShortCode()
                )
                .clickCount(
                        Long.valueOf(
                                urlMapping.getClickCount()
                        )
                )
                .createdAt(
                        urlMapping.getCreatedAt()
                )
                .build();
    }



    public DashboardStatsResponse getDashboardStats() {

        User currentUser =
                authenticationService.getCurrentUser();

        List<UrlMapping> urls =
                repository.findByUser(currentUser);

        long totalLinks = urls.size();

        long totalClicks = urls.stream()
                .mapToLong(UrlMapping::getClickCount)
                .sum();

        return DashboardStatsResponse
                .builder()
                .totalLinks(totalLinks)
                .totalClicks(totalClicks)
                .build();
    }
}
