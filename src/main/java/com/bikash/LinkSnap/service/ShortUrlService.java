package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.CreateShortUrlRequestDTO;
import com.bikash.LinkSnap.dto.ShortUrlResponseDTO;

public interface ShortUrlService {

    ShortUrlResponseDTO createShortUrl(CreateShortUrlRequestDTO request);

    String redirect(String shortCode);

}