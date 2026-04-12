package com.medilabo.auth.dto;

import java.util.List;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String username,
        List<String> roles
) {
}
