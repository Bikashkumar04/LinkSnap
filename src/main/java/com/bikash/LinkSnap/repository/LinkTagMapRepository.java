package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.LinkTagId;
import com.bikash.LinkSnap.entity.LinkTagMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkTagMapRepository extends JpaRepository<LinkTagMap, LinkTagId> {

    List<LinkTagMap> findByIdLinkId(Long linkId);

    List<LinkTagMap> findByIdTagId(Long tagId);

    Optional<LinkTagMap> findByIdLinkIdAndIdTagId(Long linkId, Long tagId);

    void deleteByIdTagId(Long tagId);
}
