package com.medilabo.assessment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "security.jwt")
public record JwtConfigProperties(
        String secret,
        long expirationMinutes
) {
}
