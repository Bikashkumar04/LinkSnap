package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.LinkClickEventDTO;
import com.bikash.LinkSnap.entity.Domain;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.repository.DomainRepository;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.service.LinkClickEventService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedirectServiceImplTest {

    @Mock
    private DomainRepository domainRepository;
    @Mock
    private LinkRepository linkRepository;
    @Mock
    private LinkClickEventService linkClickEventService;

    @InjectMocks
    private RedirectServiceImpl redirectService;

    @Test
    void resolveRedirectUrlShouldRecordClickEvent() {
        Domain domain = new Domain();
        domain.setId(5L);
        domain.setHost("go.acme.com");

        Link link = new Link();
        link.setId(10L);
        link.setDomainId(5L);
        link.setOriginalUrl("https://example.com/landing");
        link.setActive(true);
        link.setExpiresAt(LocalDateTime.now().plusHours(1));

        when(domainRepository.findByHost("go.acme.com")).thenReturn(Optional.of(domain));
        when(linkRepository.findByDomainIdAndShortCode(5L, "abcd12")).thenReturn(Optional.of(link));

        String url = redirectService.resolveRedirectUrl(
                "go.acme.com", "abcd12", "127.0.0.1", "https://google.com",
                "Mozilla/5.0", "google", "cpc", "campaign-1"
        );

        assertEquals("https://example.com/landing", url);

        ArgumentCaptor<LinkClickEventDTO> eventCaptor = ArgumentCaptor.forClass(LinkClickEventDTO.class);
        verify(linkClickEventService).recordClickEvent(eventCaptor.capture());
        assertEquals(10L, eventCaptor.getValue().getLinkId());
        assertNotNull(eventCaptor.getValue().getIpHash());
    }

    @Test
    void resolveRedirectUrlShouldBlockWhenMaxClicksReached() {
        Domain domain = new Domain();
        domain.setId(5L);
        domain.setHost("go.acme.com");

        Link link = new Link();
        link.setId(10L);
        link.setDomainId(5L);
        link.setOriginalUrl("https://example.com/landing");
        link.setActive(true);
        link.setMaxClicks(3);

        when(domainRepository.findByHost("go.acme.com")).thenReturn(Optional.of(domain));
        when(linkRepository.findByDomainIdAndShortCode(5L, "abcd12")).thenReturn(Optional.of(link));
        when(linkClickEventService.countClicks(10L)).thenReturn(3L);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> redirectService.resolveRedirectUrl(
                        "go.acme.com", "abcd12", "127.0.0.1", null,
                        "Mozilla/5.0", null, null, null
                )
        );

        assertEquals("Link has reached max click limit", ex.getMessage());
        verify(linkClickEventService, never()).recordClickEvent(any());
    }
}
