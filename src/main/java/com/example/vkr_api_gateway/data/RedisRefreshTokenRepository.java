package com.example.vkr_api_gateway.data;

import com.example.vkr_api_gateway.business.RefreshToken;
import com.example.vkr_api_gateway.business.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository implements RefreshTokenRepository {

    private final ReactiveRedisTemplate<String, RefreshToken> redisTemplate;

    private String key(Long userId, String deviceId) {
        return "auth:refresh:" + userId + ":" + deviceId;
    }

    public Mono<Boolean> save(RefreshToken session, Duration ttl) {
        return redisTemplate.opsForValue()
                .set(key(session.getUserId(), session.getDeviceId()), session, ttl);
    }

    public Mono<RefreshToken> findByUserIdAndDeviceId(Long userId, String deviceId) {
        return redisTemplate.opsForValue().get(key(userId, deviceId));
    }

    public Mono<Boolean> deleteByUserIdAndDeviceId(Long userId, String deviceId) {
        return redisTemplate.delete(key(userId, deviceId)).map(count -> count > 0);
    }
}
