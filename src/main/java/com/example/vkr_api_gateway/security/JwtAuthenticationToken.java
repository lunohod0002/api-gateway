package com.example.vkr_api_gateway.security;


import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;
    private final String token;

    public JwtAuthenticationToken(Long userId, String token, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.token = token;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }
}