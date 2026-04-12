package com.medilabo.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtConfigProperties(
        String secret,
        long expirationMinutes
) {
}
