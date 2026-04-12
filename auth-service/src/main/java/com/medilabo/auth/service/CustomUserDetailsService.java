package com.medilabo.auth.service;

import java.util.stream.Collectors;

import com.medilabo.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> User.withUsername(user.getUsername())
                        .password(user.getPasswordHash())
                        .disabled(!user.isEnabled())
                        .authorities(
                                user.getRoles().stream()
                                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                        .collect(Collectors.toSet())
                        )
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable : " + username));
    }
}