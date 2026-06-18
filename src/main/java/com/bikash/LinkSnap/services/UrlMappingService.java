package com.bikash.LinkSnap.services;

import com.bikash.LinkSnap.dto.UrlMappingDto;
import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.repository.UrlMappingRepository;
import com.bikash.LinkSnap.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private final UrlMappingRepository repository;
    private final Base62Encoder base62Encoder;

    public UrlMappingDto shortenUrl(UrlMappingDto dto) {

        UrlMapping urlMapping = new UrlMapping();

        urlMapping.setOriginalUrl(dto.getOriginalUrl());

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
}
