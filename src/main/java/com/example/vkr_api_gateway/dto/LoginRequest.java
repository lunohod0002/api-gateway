package com.example.vkr_api_gateway.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    /**
     * Клиент обязан прислать deviceId.
     * Например UUID, сохраненный на клиенте.
     */
    @NotBlank
    private String deviceId;
}