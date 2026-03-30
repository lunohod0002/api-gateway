package com.example.vkr_api_gateway.config;

import com.example.vkr_api_gateway.security.JwtAuthenticationWebFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .authorizeExchange(exchanges -> exchanges

                        // auth endpoints
                        .pathMatchers(HttpMethod.POST, "/auth/login", "/auth/refresh").permitAll()
                        .pathMatchers(HttpMethod.POST, "/auth/logout").authenticated()

                        // admin only
                        .pathMatchers(HttpMethod.POST, "/api/attractions").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/medias").hasRole("ADMIN")
                        // всё остальное публично
                        .anyExchange().permitAll()
                )
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}