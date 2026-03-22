package com.example.vkr_api_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshSession {
    private Long userId;
    private String deviceId;
    private String refreshTokenJti;
    private String refreshTokenHash;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean revoked;
}