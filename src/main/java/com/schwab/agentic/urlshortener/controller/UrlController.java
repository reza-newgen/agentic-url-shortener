package com.schwab.agentic.urlshortener.controller;


import com.schwab.agentic.urlshortener.service.*;
import com.schwab.agentic.urlshortener.dto.*;
import com.schwab.agentic.urlshortener.entity.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import jakarta.validation.Valid;

@RestController
public class UrlController {
    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/urls")
    public UrlLink create(@Valid @RequestBody CreateUrlRequest input) {
        return service.create(input.url(), input.expiresAt());
    }

    @GetMapping("/api/urls/{code}/analytics")
    public UrlLink analytics(@PathVariable String code) {
        return service.analytics(code);
    }

    @GetMapping("/urls/{code}")
    public ResponseEntity<Void> redirect(@PathVariable String code) {
        return ResponseEntity.status(HttpStatus.FOUND).location(java.net.URI.create(service.resolve(code))).build();
    }
}
