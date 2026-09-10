package com.opstrack.security;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
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
}