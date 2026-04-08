package com.example.vkr_api_gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("station-service-id", r -> r
                        .path("/api/stations/**")
                        .uri("http://localhost:8081"))
                .route("station-attraction-service-id", r -> r
                        .path("/api/attractions/**")
                        .uri("http://localhost:8081"))
                .route("media-service-id", r -> r
                    .path("/api/medias/**")
                    .uri("http://localhost:8082"))
                .build();
    }
}

