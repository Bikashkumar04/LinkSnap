package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.LinkClickEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LinkClickEventRepository extends JpaRepository<LinkClickEvent, Long> {

    Page<LinkClickEvent> findByLinkId(Long linkId, Pageable pageable);

    long countByLinkId(Long linkId);

    long countByLinkIdAndClickedAtBetween(Long linkId, LocalDateTime from, LocalDateTime to);

    List<LinkClickEvent> findByClickedAtBetween(LocalDateTime from, LocalDateTime to);
}
