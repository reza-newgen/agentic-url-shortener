package com.schwab.agentic.urlshortener.repository;


import com.schwab.agentic.urlshortener.entity.UrlLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UrlRepository extends JpaRepository<UrlLink, Long> {
    Optional<UrlLink> findByCode(String code);
}