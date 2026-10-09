package com.schwab.agentic.urlshortener.entity;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "url_link")
public class UrlLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(unique = true, nullable = false)
    public String code;
    @Column(name = "original_url", nullable = false, columnDefinition = "text")
    public String originalUrl;
    @Column(name = "created_at", nullable = false)
    public Instant createdAt = Instant.now();
    @Column(name = "expires_at")
    public Instant expiresAt;
    public long clicks;
}
