package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.ClickEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
}