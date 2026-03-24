package com.example.vkr_api_gateway.controller;

import com.example.vkr_api_gateway.dto.LoginRequest;
import com.example.vkr_api_gateway.dto.LogoutRequest;
import com.example.vkr_api_gateway.dto.RefreshRequest;
import com.example.vkr_api_gateway.dto.TokenResponse;
import jakarta.validation.Valid;
import com.example.vkr_api_gateway.service.AuthService;
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
    public Mono<ResponseEntity<TokenResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@Valid @RequestBody LogoutRequest request) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (Long) ctx.getAuthentication().getPrincipal())
                .flatMap(userId -> authService.logout(userId, request.getDeviceId()))
                .thenReturn(ResponseEntity.noContent().build());
    }
}