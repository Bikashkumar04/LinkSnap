package com.bikash.LinkSnap.repository;

import com.bikash.LinkSnap.entity.ShortUrls;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrls, Long> {

    Optional<ShortUrls> findByShortCode(String shortCode);

}