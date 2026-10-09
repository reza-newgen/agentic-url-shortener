package com.schwab.agentic.urlshortener.dto;


import jakarta.validation.constraints.*;import java.time.Instant;
public record CreateUrlRequest(@NotBlank String url,Instant expiresAt){}