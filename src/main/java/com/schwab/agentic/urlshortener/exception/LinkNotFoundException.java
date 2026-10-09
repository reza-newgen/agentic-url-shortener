package com.schwab.agentic.urlshortener.exception;


public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String code) {
        super("URL missing or expired: " + code);
    }
}