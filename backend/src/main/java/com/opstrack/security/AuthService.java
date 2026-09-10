package com.opstrack.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser registerUser(
            String username,
            String rawPassword,
            Role role
    ) {
        if (appUserRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException(
                    "Username already exists: " + username
            );
        }

        String encodedPassword =
                passwordEncoder.encode(rawPassword);

        AppUser appUser = new AppUser(
                username,
                encodedPassword,
                role,
                true
        );

        return appUserRepository.save(appUser);
    }
}