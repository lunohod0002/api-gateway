package com.example.vkr_api_gateway.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequest {
    @NotBlank
    private String deviceId;
}