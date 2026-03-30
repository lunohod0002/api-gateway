package com.example.vkr_api_gateway.service;


import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private final Map<String, UserRecord> users = Map.of(
            "admin", UserRecord.builder()
                    .id(1L)
                    .username("admin")
                    .passwordHash(new BCryptPasswordEncoder().encode("admin123"))
                    .roles(List.of("ROLE_ADMIN"))
                    .build()
    );

    public Mono<UserRecord> validate(String username, String rawPassword) {
        UserRecord user = users.get(username);
        if (user == null) {
            return Mono.empty();
        }
        return encoder.matches(rawPassword, user.getPasswordHash())
                ? Mono.just(user)
                : Mono.empty();
    }

    @Data
    @Builder
    public static class UserRecord {
        private Long id;
        private String username;
        private String passwordHash;
        private List<String> roles;
    }
}