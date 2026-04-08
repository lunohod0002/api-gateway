package com.example.vkr_api_gateway.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        HttpStatus status = switch (ex.getMessage()) {
            case "Invalid credentials", "Session not found", "Session revoked",
                 "Refresh token JTI mismatch", "Refresh token hash mismatch",
                 "Device mismatch", "Invalid refresh token type" -> HttpStatus.UNAUTHORIZED;
            default -> HttpStatus.BAD_REQUEST;
        };

        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "error", ex.getMessage()
        ));
    }
}