package com.example.vkr_api_gateway.application.services;

import com.example.vkr_api_gateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, String deviceId, List<String> roles) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.getAccessTtlMinutes() * 60);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "access")
                .claim("deviceId", deviceId)
                .claim("roles", roles)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    public String generateRefreshToken(Long userId, String deviceId) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtProperties.getRefreshTtlDays() * 24 * 60 * 60);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("type", "refresh")
                .claim("deviceId", deviceId)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isAccessToken(Claims claims) {
        return "access".equals(claims.get("type", String.class));
    }

    public boolean isRefreshToken(Claims claims) {
        return "refresh".equals(claims.get("type", String.class));
    }

    public Long getUserId(Claims claims) {
        return Long.valueOf(claims.getSubject());
    }

    public String getDeviceId(Claims claims) {
        return claims.get("deviceId", String.class);
    }

    public List<String> getRoles(Claims claims) {
        return claims.get("roles", List.class);
    }

    public String getJti(Claims claims) {
        return claims.getId();
    }

    public long getAccessTtlSeconds() {
        return jwtProperties.getAccessTtlMinutes() * 60;
    }

    public long getRefreshTtlSeconds() {
        return jwtProperties.getRefreshTtlDays() * 24 * 60 * 60;
    }
}