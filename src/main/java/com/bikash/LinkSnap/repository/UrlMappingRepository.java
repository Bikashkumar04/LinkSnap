package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.UrlMapping;
import com.bikash.LinkSnap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    Optional<UrlMapping> findByShortCode(String shortCode);

    List<UrlMapping> findByUser(User user);

    Optional<UrlMapping> findByIdAndUser(
            Long id,
            User user
    );
}