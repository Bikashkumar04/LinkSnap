package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.UrlMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
}