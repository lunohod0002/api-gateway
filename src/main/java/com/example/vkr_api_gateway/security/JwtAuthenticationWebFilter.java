package com.example.vkr_api_gateway.security;


import com.example.vkr_api_gateway.service.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationWebFilter implements WebFilter {

    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();

        if (path.startsWith("/auth/")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return chain.filter(exchange);
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtService.parse(token);

            if (!jwtService.isAccessToken(claims)) {
                return chain.filter(exchange);
            }

            Long userId = jwtService.getUserId(claims);
            String deviceId = jwtService.getDeviceId(claims);
            List<String> roles = jwtService.getRoles(claims);

            var authorities = roles == null ? List.<SimpleGrantedAuthority>of()
                    : roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

            JwtAuthenticationToken authentication = new JwtAuthenticationToken(userId, token, authorities);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header("X-User-Id", String.valueOf(userId))
                    .header("X-Device-Id", deviceId)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build())
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));

        } catch (Exception e) {
            return chain.filter(exchange);
        }
    }
}