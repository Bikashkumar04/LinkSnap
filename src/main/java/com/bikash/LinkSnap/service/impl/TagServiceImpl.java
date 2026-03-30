package com.bikash.LinkSnap.service.impl;

import com.bikash.LinkSnap.dto.TagDTO;
import com.bikash.LinkSnap.entity.Link;
import com.bikash.LinkSnap.entity.LinkTagId;
import com.bikash.LinkSnap.entity.LinkTagMap;
import com.bikash.LinkSnap.entity.Tag;
import com.bikash.LinkSnap.repository.LinkRepository;
import com.bikash.LinkSnap.repository.LinkTagMapRepository;
import com.bikash.LinkSnap.repository.TagRepository;
import com.bikash.LinkSnap.service.TagService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final LinkRepository linkRepository;
    private final LinkTagMapRepository linkTagMapRepository;

    public TagServiceImpl(
            TagRepository tagRepository,
            LinkRepository linkRepository,
            LinkTagMapRepository linkTagMapRepository
    ) {
        this.tagRepository = tagRepository;
        this.linkRepository = linkRepository;
        this.linkTagMapRepository = linkTagMapRepository;
    }

    @Override
    @Transactional
    public TagDTO createTag(Long workspaceId, TagDTO request) {
        String name = request.getName() == null ? null : request.getName().trim();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Tag name is required");
        }
        if (tagRepository.existsByWorkspaceIdAndNameIgnoreCase(workspaceId, name)) {
            throw new IllegalArgumentException("Tag already exists in workspace");
        }

        Tag tag = new Tag();
        tag.setWorkspaceId(workspaceId);
        tag.setName(name);
        tag.setColor(request.getColor());
        return toDTO(tagRepository.save(tag));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> listWorkspaceTags(Long workspaceId) {
        return tagRepository.findByWorkspaceId(workspaceId).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteTag(Long workspaceId, Long tagId) {
        Tag tag = tagRepository.findByIdAndWorkspaceId(tagId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
        linkTagMapRepository.deleteByIdTagId(tag.getId());
        tagRepository.delete(tag);
    }

    @Override
    @Transactional
    public TagDTO assignTagToLink(Long workspaceId, Long linkId, Long tagId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        if (!workspaceId.equals(link.getWorkspaceId())) {
            throw new IllegalArgumentException("Link does not belong to workspace");
        }

        Tag tag = tagRepository.findByIdAndWorkspaceId(tagId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        LinkTagId id = new LinkTagId(linkId, tagId);
        if (linkTagMapRepository.findById(id).isEmpty()) {
            linkTagMapRepository.save(new LinkTagMap(id));
        }
        return toDTO(tag);
    }

    @Override
    @Transactional
    public void removeTagFromLink(Long workspaceId, Long linkId, Long tagId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        if (!workspaceId.equals(link.getWorkspaceId())) {
            throw new IllegalArgumentException("Link does not belong to workspace");
        }
        tagRepository.findByIdAndWorkspaceId(tagId, workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

        linkTagMapRepository.findById(new LinkTagId(linkId, tagId))
                .ifPresent(linkTagMapRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDTO> listLinkTags(Long workspaceId, Long linkId) {
        Link link = linkRepository.findById(linkId)
                .orElseThrow(() -> new IllegalArgumentException("Link not found"));
        if (!workspaceId.equals(link.getWorkspaceId())) {
            throw new IllegalArgumentException("Link does not belong to workspace");
        }

        Set<Long> tagIds = linkTagMapRepository.findByIdLinkId(linkId).stream()
                .map(map -> map.getId().getTagId())
                .collect(java.util.stream.Collectors.toSet());

        return tagRepository.findAllById(tagIds).stream()
                .map(this::toDTO)
                .toList();
    }

    private TagDTO toDTO(Tag tag) {
        return new TagDTO(
                tag.getId(),
                tag.getWorkspaceId(),
                tag.getName(),
                tag.getColor(),
                tag.getCreatedAt()
        );
    }
}
