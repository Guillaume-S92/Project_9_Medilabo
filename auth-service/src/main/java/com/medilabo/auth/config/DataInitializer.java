package com.medilabo.auth.config;

import java.util.Set;

import com.medilabo.auth.document.UserDocument;
import com.medilabo.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-organizer.username:organizer}")
    private String organizerUsername;

    @Value("${app.default-organizer.password:Organizer123!}")
    private String organizerPassword;

    @Value("${app.default-practitioner.username:practitioner}")
    private String practitionerUsername;

    @Value("${app.default-practitioner.password:Practitioner123!}")
    private String practitionerPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        createIfMissing(organizerUsername, organizerPassword, Set.of("ORGANIZER"));
        createIfMissing(practitionerUsername, practitionerPassword, Set.of("PRACTITIONER"));
    }

    private void createIfMissing(String username, String rawPassword, Set<String> roles) {
        userRepository.findByUsername(username).orElseGet(() ->
                userRepository.save(new UserDocument(null, username, passwordEncoder.encode(rawPassword), roles, true)));
    }
}
