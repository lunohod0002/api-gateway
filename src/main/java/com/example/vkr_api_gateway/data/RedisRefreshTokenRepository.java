package com.example.vkr_api_gateway.data;

import com.example.vkr_api_gateway.domain.RefreshToken;
import com.example.vkr_api_gateway.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final ReactiveRedisTemplate<String, RefreshToken> redisTemplate;

    private String key(Long userId) {
        return "auth:refresh:" + userId;
    }

    public Mono<Boolean> save(RefreshToken session, Duration ttl) {
        return redisTemplate.opsForValue()
                .set(key(session.getUserId()), session, ttl);
    }

    public Mono<RefreshToken> findByUserId(Long userId) {
        return redisTemplate.opsForValue().get(key(userId));
    }

    public Mono<Boolean> deleteByUserId(Long userId) {
        return redisTemplate.delete(key(userId)).map(count -> count > 0);
    }
}
