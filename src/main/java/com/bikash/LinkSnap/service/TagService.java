package com.bikash.LinkSnap.service;

import com.bikash.LinkSnap.dto.TagDTO;

import java.util.List;

public interface TagService {

    TagDTO createTag(Long workspaceId, TagDTO request);

    List<TagDTO> listWorkspaceTags(Long workspaceId);

    void deleteTag(Long workspaceId, Long tagId);

    TagDTO assignTagToLink(Long workspaceId, Long linkId, Long tagId);

    void removeTagFromLink(Long workspaceId, Long linkId, Long tagId);

    List<TagDTO> listLinkTags(Long workspaceId, Long linkId);
}
