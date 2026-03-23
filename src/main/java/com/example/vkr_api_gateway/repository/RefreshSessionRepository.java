package com.example.vkr_api_gateway.repository;

import com.example.vkr_api_gateway.model.RefreshSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class RefreshSessionRepository {

    private final ReactiveRedisTemplate<String, RefreshSession> redisTemplate;

    private String key(Long userId, String deviceId) {
        return "auth:refresh:" + userId + ":" + deviceId;
    }

    public Mono<Boolean> save(RefreshSession session, Duration ttl) {
        return redisTemplate.opsForValue()
                .set(key(session.getUserId(), session.getDeviceId()), session, ttl);
    }

    public Mono<RefreshSession> findByUserIdAndDeviceId(Long userId, String deviceId) {
        return redisTemplate.opsForValue().get(key(userId, deviceId));
    }

    public Mono<Boolean> deleteByUserIdAndDeviceId(Long userId, String deviceId) {
        return redisTemplate.delete(key(userId, deviceId)).map(count -> count > 0);
    }
}
