package com.medilabo.auth.service;

import java.time.Instant;
import java.util.List;

import com.medilabo.auth.config.JwtConfigProperties;
import com.medilabo.auth.dto.AuthResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtEncoder jwtEncoder;
    private final JwtConfigProperties jwtConfigProperties;

    public TokenService(JwtEncoder jwtEncoder, JwtConfigProperties jwtConfigProperties) {
        this.jwtEncoder = jwtEncoder;
        this.jwtConfigProperties = jwtConfigProperties;
    }

    public AuthResponse generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtConfigProperties.expirationMinutes() * 60L);

        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .sorted()
                .toList();

        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                .issuer("auth-service")
                .subject(authentication.getName())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .claim("roles", roles)
                .build();

        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                claimsSet
        )).getTokenValue();

        return new AuthResponse(
                token,
                "Bearer",
                jwtConfigProperties.expirationMinutes() * 60L,
                authentication.getName(),
                roles
        );
    }
}