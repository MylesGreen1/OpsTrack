package com.opstrack.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;

    public AuthController(
            AuthService authService,
            AppUserRepository appUserRepository,
            AuthenticationManager authenticationManager
    ) {
        this.authService = authService;
        this.appUserRepository = appUserRepository;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public CurrentUserResponse login(
            @RequestBody LoginRequest loginRequest,
            HttpServletRequest request
    ) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                loginRequest.username(),
                                loginRequest.password()
                        )
                );

        SecurityContext securityContext =
                SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(
                authentication
        );

        SecurityContextHolder.setContext(
                securityContext
        );

        request.getSession(true)
                .setAttribute(
                        HttpSessionSecurityContextRepository
                                .SPRING_SECURITY_CONTEXT_KEY,
                        securityContext
                );

        return buildCurrentUserResponse(
                authentication.getName()
        );
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request
    ) {

        HttpSession session =
                request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(
            @RequestParam String username,
            @RequestParam String password
    ) {

        AppUser appUser =
                authService.registerUser(
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

        return buildCurrentUserResponse(
                authentication.getName()
        );
    }

    private CurrentUserResponse buildCurrentUserResponse(
            String username
    ) {

        AppUser appUser =
                appUserRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
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