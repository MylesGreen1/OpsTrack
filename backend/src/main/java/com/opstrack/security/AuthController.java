package com.opstrack.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AppUserRepository appUserRepository;

    public AuthController(
            AuthService authService,
            AppUserRepository appUserRepository
    ) {
        this.authService = authService;
        this.appUserRepository = appUserRepository;
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(
            @RequestParam String username,
            @RequestParam String password
    ) {
        AppUser appUser = authService.registerUser(
                username,
                password,
                Role.TECHNICIAN
        );

        return new RegisterResponse(
                appUser.getUsername(),
                appUser.getRole(),
                appUser.isEnabled()
        );
    }

    @GetMapping("/me")
    public CurrentUserResponse getCurrentUser(
            Authentication authentication
    ) {
        AppUser appUser =
                appUserRepository
                        .findByUsername(
                                authentication.getName()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Authenticated user not found."
                                )
                        );

        return new CurrentUserResponse(
                appUser.getUsername(),
                appUser.getRole(),
                appUser.isEnabled(),
                appUser.getTechnician() != null
                        ? appUser.getTechnician().getId()
                        : null
        );
    }
}