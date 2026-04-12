package com.medilabo.auth.controller;

import java.util.List;

import com.medilabo.auth.dto.AuthResponse;
import com.medilabo.auth.dto.LoginRequest;
import com.medilabo.auth.dto.MeResponse;
import com.medilabo.auth.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/api/auth/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.username(), request.password()));
        return tokenService.generateToken(authentication);
    }

    @GetMapping("/api/auth/me")
    public MeResponse me(Authentication authentication) {
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = jwt.getClaimAsStringList("roles");
        return new MeResponse(jwt.getSubject(), roles == null ? List.of() : roles);
    }
}
