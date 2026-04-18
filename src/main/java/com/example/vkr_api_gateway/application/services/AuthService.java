package com.example.vkr_api_gateway.application.services;


import com.example.vkr_api_gateway.application.dto.LoginRequest;
import com.example.vkr_api_gateway.application.dto.RefreshRequest;
import com.example.vkr_api_gateway.application.dto.TokenResponse;
import com.example.vkr_api_gateway.domain.RefreshToken;
import com.example.vkr_api_gateway.data.RedisRefreshTokenRepository;
import com.example.vkr_api_gateway.util.HashUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final RedisRefreshTokenRepository redisRefreshTokenRepository;

    public Mono<TokenResponse> login(LoginRequest request) {
        return userService.validate(request.getUsername(), request.getPassword())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid credentials")))
                .flatMap(user -> {
                    String accessToken = jwtService.generateAccessToken(user.getId(), request.getDeviceId(), user.getRoles());
                    String refreshToken = jwtService.generateRefreshToken(user.getId(), request.getDeviceId());

                    Claims refreshClaims = jwtService.parse(refreshToken);

                    RefreshToken session = RefreshToken.builder()
                            .userId(user.getId())
                            .deviceId(request.getDeviceId())
                            .refreshTokenJti(jwtService.getJti(refreshClaims))
                            .refreshTokenHash(HashUtils.sha256(refreshToken))
                            .createdAt(Instant.now())
                            .expiresAt(refreshClaims.getExpiration().toInstant())
                            .revoked(false)
                            .build();

                    return redisRefreshTokenRepository.save(
                                    session,
                                    Duration.ofSeconds(jwtService.getRefreshTtlSeconds())
                            )
                            .thenReturn(TokenResponse.builder()
                                    .tokenType("Bearer")
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken)
                                    .accessExpiresIn(jwtService.getAccessTtlSeconds())
                                    .refreshExpiresIn(jwtService.getRefreshTtlSeconds())
                                    .deviceId(request.getDeviceId())
                                    .build());
                });
    }

    public Mono<TokenResponse> refresh(RefreshRequest request) {
        return Mono.fromCallable(() -> jwtService.parse(request.getRefreshToken()))
                .flatMap(claims -> {
                    if (!jwtService.isRefreshToken(claims)) {
                        return Mono.error(new RuntimeException("Invalid refresh token type"));
                    }

                    Long userId = jwtService.getUserId(claims);
                    String tokenDeviceId = jwtService.getDeviceId(claims);
                    String jti = jwtService.getJti(claims);

                    if (!request.getDeviceId().equals(tokenDeviceId)) {
                        return Mono.error(new RuntimeException("Device mismatch"));
                    }

                    return redisRefreshTokenRepository.findByUserIdAndDeviceId(userId, request.getDeviceId())
                            .switchIfEmpty(Mono.error(new RuntimeException("Session not found")))
                            .flatMap(session -> {
                                if (session.isRevoked()) {
                                    return Mono.error(new RuntimeException("Session revoked"));
                                }

                                String incomingHash = HashUtils.sha256(request.getRefreshToken());

                                if (!session.getRefreshTokenJti().equals(jti)) {
                                    return Mono.error(new RuntimeException("Refresh token JTI mismatch"));
                                }

                                if (!session.getRefreshTokenHash().equals(incomingHash)) {
                                    return Mono.error(new RuntimeException("Refresh token hash mismatch"));
                                }

                                // Для примера роль зашиваем по userId
                                var roles = userId == 1L
                                        ? java.util.List.of("ROLE_ADMIN")
                                        : java.util.List.of("ROLE_USER");

                                String newAccessToken = jwtService.generateAccessToken(userId, request.getDeviceId(), roles);
                                String newRefreshToken = jwtService.generateRefreshToken(userId, request.getDeviceId());
                                Claims newRefreshClaims = jwtService.parse(newRefreshToken);

                                RefreshToken newSession = RefreshToken.builder()
                                        .userId(userId)
                                        .deviceId(request.getDeviceId())
                                        .refreshTokenJti(jwtService.getJti(newRefreshClaims))
                                        .refreshTokenHash(HashUtils.sha256(newRefreshToken))
                                        .createdAt(Instant.now())
                                        .expiresAt(newRefreshClaims.getExpiration().toInstant())
                                        .revoked(false)
                                        .build();

                                return redisRefreshTokenRepository.save(
                                                newSession,
                                                Duration.ofSeconds(jwtService.getRefreshTtlSeconds())
                                        )
                                        .thenReturn(TokenResponse.builder()
                                                .tokenType("Bearer")
                                                .accessToken(newAccessToken)
                                                .refreshToken(newRefreshToken)
                                                .accessExpiresIn(jwtService.getAccessTtlSeconds())
                                                .refreshExpiresIn(jwtService.getRefreshTtlSeconds())
                                                .deviceId(request.getDeviceId())
                                                .build());
                            });
                });
    }

    public Mono<Void> logout(Long userId, String deviceId) {
        return redisRefreshTokenRepository.deleteByUserIdAndDeviceId(userId, deviceId).then();
    }
}