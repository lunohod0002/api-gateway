package com.example.vkr_api_gateway.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class RefreshResponse {
    private String accessToken;
}