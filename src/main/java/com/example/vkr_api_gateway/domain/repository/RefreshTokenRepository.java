package com.example.vkr_api_gateway.domain.repository;

import com.example.vkr_api_gateway.domain.RefreshToken;
import reactor.core.publisher.Mono;

import java.time.Duration;


public interface RefreshTokenRepository {

    public Mono<Boolean> save(RefreshToken session, Duration ttl) ;
    public Mono<RefreshToken> findByUserId(Long userId);

    public Mono<Boolean> deleteByUserId(Long userId);
}
