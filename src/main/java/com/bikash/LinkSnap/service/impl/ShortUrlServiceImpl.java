package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;
import com.bikash.LinkSnap.entity.ShortUrls;
import com.bikash.LinkSnap.repository.ShortUrlRepository;
import com.bikash.LinkSnap.service.ShortUrlService;
import com.bikash.LinkSnap.util.Base62Encoder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository repository;

    @Value("${linksnap.domain}")
    private String domain;

    public ShortUrlServiceImpl(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Override
    public ShortUrlResponseDTO createShortUrl(CreateShortUrlRequestDTO request) {

        ShortUrls url = new ShortUrls();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiryAt(request.getExpiryAt());
        url.setClickCount(0);
        url.setMaxClicks(request.getMaxClicks());
        url.setUserId(request.getUserId());
        url.setActive(true);

        String shortCode;

        // Case 1: Custom short code provided
        if (request.getCustomShortCode() != null && !request.getCustomShortCode().isBlank()) {

            shortCode = request.getCustomShortCode();

            validateShortCode(shortCode);

            if (repository.existsByShortCode(shortCode)) {
                throw new IllegalArgumentException("Custom short code already exists");
            }

        } else {

            // Case 2: Generate random short code
            do {
                shortCode = Base62Encoder.generateRandom(6);
            } while (repository.existsByShortCode(shortCode));
        }

        url.setShortCode(shortCode);

        ShortUrls savedUrl = repository.save(url);

        return new ShortUrlResponseDTO(
                domain + "/" + shortCode,
                savedUrl.getOriginalUrl(),
                savedUrl.getClickCount()
        );
    }

    @Override
    public String redirect(String shortCode) {

        ShortUrls url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new IllegalArgumentException("Short URL not found"));

        // Check inactive link
        if (!url.isActive()) {
            throw new IllegalStateException("Short URL is inactive");
        }

        // Check expiry
        if (url.getExpiryAt() != null && url.getExpiryAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Short URL has expired");
        }
        if (url.getMaxClicks() != null && url.getClickCount() >= url.getMaxClicks()) {
            throw new IllegalStateException("Short URL has reached max click limit");
        }

        // Increase click count
        url.setClickCount(url.getClickCount() + 1);
        repository.save(url);

        return url.getOriginalUrl();
    }

    private void validateShortCode(String code) {

        if (!code.matches("^[a-zA-Z0-9_-]{4,20}$")) {
            throw new IllegalArgumentException(
                    "Short code must be 4-20 characters and contain only letters, numbers, - or _"
            );
        }
    }
}
