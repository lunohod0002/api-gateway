package com.example.vkr_api_gateway.controller;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class DemoController {

    @GetMapping("/me")
    public Mono<Map<String, Object>> me() {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> Map.of(
                        "userId", ctx.getAuthentication().getPrincipal(),
                        "authorities", ctx.getAuthentication().getAuthorities()
                ));
    }
}
