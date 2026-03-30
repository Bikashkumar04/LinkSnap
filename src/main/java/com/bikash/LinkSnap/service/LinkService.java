package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.LinkDTO;

import java.util.List;

public interface LinkService {

    LinkDTO createLink(LinkDTO request);

    LinkDTO updateLink(Long linkId, LinkDTO request);

    LinkDTO pauseLink(Long linkId, boolean active);

    void deleteLink(Long linkId);

    LinkDTO getLinkById(Long linkId);

    List<LinkDTO> listWorkspaceLinks(Long workspaceId);
}
