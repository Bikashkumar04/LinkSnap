package com.bikash.LinkSnap.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShortUrlResponseDTO {

    private String shortUrl;
    private String originalUrl;
    private int clickCount;

}