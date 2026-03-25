package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;
import com.bikash.LinkSnap.entity.ShortUrls;
import com.bikash.LinkSnap.repository.ShortUrlRepository;
import com.bikash.LinkSnap.service.ShortUrlService;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final ShortUrlRepository repository;

    public ShortUrlServiceImpl(ShortUrlRepository repository) {
        this.repository = repository;
    }

    @Override
    public ShortUrlResponseDTO createShortUrl(CreateShortUrlRequestDTO request) {

        String shortCode = UUID.randomUUID().toString().substring(0, 6);

        ShortUrls url = new ShortUrls();
        url.setOriginalUrl(request.getOriginalUrl());
        url.setShortCode(shortCode);
        url.setCreatedAt(LocalDateTime.now());
        url.setExpiryAt(request.getExpiryAt());
        url.setClickCount(0);
        url.setUserId(request.getUserId());
        url.setActive(true);

        repository.save(url);

        return new ShortUrlResponseDTO(
                "http://localhost:8080/" + shortCode,
                url.getOriginalUrl(),
                url.getClickCount()
        );
    }

    @Override
    public String redirect(String shortCode) {

        Optional<ShortUrls> url = repository.findByShortCode(shortCode);

        if(url.isEmpty()){
            throw new RuntimeException("URL not found");
        }

        ShortUrls shortUrl = url.get();

        shortUrl.setClickCount(shortUrl.getClickCount() + 1);

        repository.save(shortUrl);

        return shortUrl.getOriginalUrl();
    }
}