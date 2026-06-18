package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.ClickEvent;
import com.bikash.LinkSnap.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlMapping(UrlMapping urlMapping);
}