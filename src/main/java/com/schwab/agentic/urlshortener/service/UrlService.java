package com.schwab.agentic.urlshortener.service;


import com.schwab.agentic.urlshortener.repository.*;
import com.schwab.agentic.urlshortener.entity.*;
import com.schwab.agentic.urlshortener.exception.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.net.*;
import java.time.*;
import java.util.*;

@Service
public class UrlService {
    private final UrlRepository repository;
    private final StringRedisTemplate cache;

    public UrlService(UrlRepository repository, StringRedisTemplate cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Transactional
    public UrlLink create(String url, Instant expires) {
        try {
            URI u = URI.create(url);
            if (!Set.of("http", "https").contains(u.getScheme()) || u.getHost() == null || u.getUserInfo() != null)
                throw new IllegalArgumentException("HTTP(S) URL required");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL", e);
        }
        if (expires != null && !expires.isAfter(Instant.now()))
            throw new IllegalArgumentException("Expiration must be in future");
        UrlLink item = new UrlLink();
        item.originalUrl = url;
        item.expiresAt = expires;
        item.code = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        return repository.save(item);
    }

    @Transactional
    public String resolve(String code) {
        UrlLink link = repository.findByCode(code).orElseThrow(() -> new LinkNotFoundException(code));
        if (link.expiresAt != null && !link.expiresAt.isAfter(Instant.now())) throw new LinkNotFoundException(code);
        link.clicks++;
        repository.save(link);
        try {
            cache.opsForValue().set("url:" + code, link.originalUrl, Duration.ofMinutes(10));
        } catch (Exception ignored) {
        } // fail open on cache outage
        return link.originalUrl;
    }

    @Transactional(readOnly = true)
    public UrlLink analytics(String code) {
        return repository.findByCode(code).orElseThrow(() -> new LinkNotFoundException(code));
    }
}
