package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.LinkDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface LinkService {

    LinkDTO createLink(LinkDTO request);

    LinkDTO updateLink(Long linkId, LinkDTO request);

    LinkDTO pauseLink(Long linkId, boolean active);

    void deleteLink(Long linkId);

    LinkDTO getLinkById(Long linkId);

    List<LinkDTO> listWorkspaceLinks(Long workspaceId);

    String buildShortLink(Long linkId);

    Page<LinkDTO> searchDashboardLinks(
            Long workspaceId,
            Long currentUserId,
            boolean mineOnly,
            Boolean active,
            Long tagId,
            String search,
            Pageable pageable
    );
}
