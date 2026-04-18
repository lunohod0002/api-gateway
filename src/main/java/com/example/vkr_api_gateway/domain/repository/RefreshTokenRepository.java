package com.example.vkr_api_gateway.domain.repository;

import com.example.vkr_api_gateway.domain.RefreshToken;
import reactor.core.publisher.Mono;

import java.time.Duration;


public interface RefreshTokenRepository {

    public Mono<Boolean> save(RefreshToken session, Duration ttl) ;
    public Mono<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId);

    public Mono<Boolean> deleteByUserIdAndDeviceId(Long userId, String deviceId);
}
