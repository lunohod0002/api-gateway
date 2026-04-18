package com.example.vkr_api_gateway.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    private Long userId;
    private String deviceId;
    private String refreshTokenJti;
    private String refreshTokenHash;
    private Instant createdAt;
    private Instant expiresAt;
    private boolean revoked;
}