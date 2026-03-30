package com.bikash.LinkSnap.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "link_click_events",
        indexes = {
                @Index(name = "idx_link_click_events_link_id", columnList = "link_id"),
                @Index(name = "idx_link_click_events_clicked_at", columnList = "clicked_at"),
                @Index(name = "idx_link_click_events_link_clicked_at", columnList = "link_id, clicked_at")
        }
)
public class LinkClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "link_id", nullable = false)
    private Long linkId;

    @Column(name = "clicked_at", nullable = false)
    private LocalDateTime clickedAt;

    @Column(name = "ip_hash", length = 128)
    private String ipHash;

    @Column(name = "country_code", length = 10)
    private String countryCode;

    @Column(length = 120)
    private String region;

    @Column(length = 120)
    private String city;

    @Column(length = 1000)
    private String referer;

    @Column(name = "user_agent", length = 1024)
    private String userAgent;

    @Column(name = "device_type", length = 40)
    private String deviceType;

    @Column(length = 80)
    private String browser;

    @Column(length = 80)
    private String os;

    @Column(name = "utm_source", length = 120)
    private String utmSource;

    @Column(name = "utm_medium", length = 120)
    private String utmMedium;

    @Column(name = "utm_campaign", length = 120)
    private String utmCampaign;

    @Column(name = "is_bot", nullable = false)
    private boolean isBot;

    @PrePersist
    void onCreate() {
        if (this.clickedAt == null) {
            this.clickedAt = LocalDateTime.now();
        }
    }
}
