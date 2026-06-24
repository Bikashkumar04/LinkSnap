package com.bikash.LinkSnap.services;

import com.bikash.LinkSnap.dto.*;
import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.entity.User;
import com.bikash.LinkSnap.repository.ClickEventRepository;
import com.bikash.LinkSnap.repository.UrlMappingRepository;
import com.bikash.LinkSnap.security.AuthenticationService;
import com.bikash.LinkSnap.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository repository;
    private final Base62Encoder base62Encoder;
    private final AuthenticationService authenticationService;
    private final ModelMapper modelMapper;


    @Value("${backend.url}")
    private String backendUrl;

    public UrlMappingDto shortenUrl(UrlMappingDto dto) {

        User currentUser =
                authenticationService.getCurrentUser();

        validateExpiry(dto);

        UrlMapping urlMapping =
                new UrlMapping();

        urlMapping.setOriginalUrl(
                dto.getOriginalUrl()
        );

        urlMapping.setUser(currentUser);

        urlMapping.setExpiryType(
                dto.getExpiryType()
        );

        urlMapping.setExpiresAt(
                calculateExpiry(dto)
        );

        UrlMapping saved =
                repository.save(urlMapping);

        String shortCode;

        if (dto.getCustomAlias() != null
                && !dto.getCustomAlias().isBlank()) {

            if (repository.findByShortCode(
                    dto.getCustomAlias()
            ).isPresent()) {

                throw new RuntimeException(
                        "Alias already exists"
                );
            }

            shortCode = dto.getCustomAlias();

        } else {

            shortCode =
                    base62Encoder.encode(
                            saved.getId()
                    );
        }

        saved.setShortCode(shortCode);

        saved = repository.save(saved);

        UrlMappingDto response =
                new UrlMappingDto();

        response.setOriginalUrl(
                saved.getOriginalUrl()
        );

        response.setCustomAlias(
                dto.getCustomAlias()
        );

        response.setExpiryType(
                saved.getExpiryType()
        );

        response.setCustomExpiryAt(
                saved.getExpiresAt()
        );

        response.setShortCode(
                saved.getShortCode()
        );

        response.setShortUrl(
                backendUrl
                        + saved.getShortCode()
        );

        return response;
    }


    // this method will return all the URL mappings created by the currently authenticated user
    // for example id	username
    //1	bikash
    //2	john
    public List<LinkResponse> getMyLinks() {

        User currentUser =
                authenticationService.getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .map(url ->
                        modelMapper.map(
                                url,
                                LinkResponse.class
                        )
                )
                .toList();
    }


    //Get Single Link
    public LinkResponse getLink(Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping urlMapping =
                repository
                        .findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Link not found"
                                ));

        return modelMapper.map(
                urlMapping,
                LinkResponse.class
        );
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
    public LinkResponse updateLink(
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
                                "Link not found"
                        ));

        urlMapping.setOriginalUrl(
                request.getOriginalUrl()
        );

        UrlMapping updated =
                repository.save(urlMapping);

        return modelMapper.map(
                updated,
                LinkResponse.class
        );
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
                        urlMapping.getClickCount()
                )
                .createdAt(
                        urlMapping.getCreatedAt()
                )
                .expiryType(
                        urlMapping.getExpiryType()
                )
                .expiresAt(
                        urlMapping.getExpiresAt()
                )
                .expired(
                        urlMapping.getExpiresAt() != null
                                &&
                                LocalDateTime.now()
                                        .isAfter(
                                                urlMapping.getExpiresAt()
                                        )
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
                .mapToLong(
                        UrlMapping::getClickCount
                )
                .sum();

        long expiredLinks = urls.stream()
                .filter(url ->
                        url.getExpiresAt() != null
                                &&
                                LocalDateTime.now()
                                        .isAfter(
                                                url.getExpiresAt()
                                        ))
                .count();

        long activeLinks =
                totalLinks - expiredLinks;

        return DashboardStatsResponse
                .builder()
                .totalLinks(totalLinks)
                .totalClicks(totalClicks)
                .activeLinks(activeLinks)
                .expiredLinks(expiredLinks)
                .build();
    }



    public List<TopLinkResponse> getTopLinks() {

        User currentUser =
                authenticationService.getCurrentUser();

        return repository.findByUser(currentUser)
                .stream()
                .sorted(
                        Comparator.comparing(
                                UrlMapping::getClickCount
                        ).reversed()
                )
                .limit(5)
                .map(url ->
                        TopLinkResponse.builder()
                                .id(url.getId())
                                .shortCode(
                                        url.getShortCode()
                                )
                                .originalUrl(
                                        url.getOriginalUrl()
                                )
                                .clickCount(
                                        url.getClickCount()
                                )
                                .build()
                )
                .toList();
    }



    private final ClickEventRepository clickEventRepository;

    public List<ClickEventResponse>
    getClickHistory(Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping urlMapping =
                repository.findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow();

        return clickEventRepository
                .findByUrlMapping(urlMapping)
                .stream()
                .map(event ->
                        ClickEventResponse
                                .builder()
                                .clickTime(
                                        event.getClickTime()
                                )
                                .ipAddress(
                                        event.getIpAddress()
                                )
                                .userAgent(
                                        event.getUserAgent()
                                )
                                .build()
                )
                .toList();
    }




    //QR code generation
    private final QRCodeService qrCodeService;

    public byte[] generateQrCode(Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping urlMapping =
                repository.findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "URL not found"
                                ));

        String shortUrl =
                backendUrl
                        + urlMapping.getShortCode();

        return qrCodeService.generateQRCode(
                shortUrl
        );
    }


    public QrCodeResponse getQrInfo(Long id) {

        User currentUser =
                authenticationService.getCurrentUser();

        UrlMapping url =
                repository.findByIdAndUser(
                                id,
                                currentUser
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "URL not found"));

        return QrCodeResponse.builder()
                .originalUrl(
                        url.getOriginalUrl()
                )
                .shortCode(
                        url.getShortCode()
                )
                .shortUrl(
                        backendUrl
                                + url.getShortCode()
                )
                .clickCount(
                        url.getClickCount()
                )
                .build();
    }


    private void validateExpiry(
            UrlMappingDto dto) {

        if (dto.getExpiryType() ==
                ExpiryType.CUSTOM &&
                dto.getCustomExpiryAt() == null) {

            throw new RuntimeException(
                    "Custom expiry date required"
            );
        }

        if (dto.getCustomExpiryAt() != null &&
                dto.getCustomExpiryAt()
                        .isBefore(
                                LocalDateTime.now()
                        )) {

            throw new RuntimeException(
                    "Expiry date must be in future"
            );
        }
    }


    private LocalDateTime calculateExpiry(
            UrlMappingDto dto) {

        if (dto.getExpiryType() == null ||
                dto.getExpiryType()
                        == ExpiryType.LIFETIME) {

            return null;
        }

        return switch (
                dto.getExpiryType()) {

            case ONE_HOUR ->
                    LocalDateTime.now()
                            .plusHours(1);

            case ONE_DAY ->
                    LocalDateTime.now()
                            .plusDays(1);

            case ONE_WEEK ->
                    LocalDateTime.now()
                            .plusWeeks(1);

            case ONE_MONTH ->
                    LocalDateTime.now()
                            .plusMonths(1);

            case CUSTOM ->
                    dto.getCustomExpiryAt();

            default -> null;
        };
    }
}
