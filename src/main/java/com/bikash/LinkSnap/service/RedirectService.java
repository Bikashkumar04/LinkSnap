package com.bikash.LinkSnap.service;

public interface RedirectService {

    String resolveRedirectUrl(
            String host,
            String shortCode,
            String ipAddress,
            String referer,
            String userAgent,
            String utmSource,
            String utmMedium,
            String utmCampaign
    );
}
