package com.example.vkr_api_gateway.controllers;

import com.example.vkr_api_gateway.application.dto.*;
import jakarta.validation.Valid;
import com.example.vkr_api_gateway.application.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<ResponseEntity<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<RefreshResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request)
                .map(ResponseEntity::ok);
    }

}