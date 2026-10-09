package com.schwab.agentic.urlshortener.exception;


import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(LinkNotFoundException.class)
    public ResponseEntity<String> missing(Exception e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, SecurityException.class})
    public ResponseEntity<String> invalid(Exception e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
