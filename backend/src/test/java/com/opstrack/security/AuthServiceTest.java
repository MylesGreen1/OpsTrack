package com.opstrack.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        String username = "tech2";
        String rawPassword = "Password123!";
        String encodedPassword = "encoded-password";

        when(appUserRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(rawPassword))
                .thenReturn(encodedPassword);

        when(appUserRepository.save(any(AppUser.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = authService.registerUser(
                username,
                rawPassword,
                Role.TECHNICIAN
        );

        assertEquals(username, result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        assertEquals(Role.TECHNICIAN, result.getRole());
        assertTrue(result.isEnabled());

        verify(passwordEncoder).encode(rawPassword);
        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    void shouldRejectDuplicateUsername() {
        String username = "tech2";

        AppUser existingUser = new AppUser(
                username,
                "existing-password",
                Role.TECHNICIAN,
                true
        );

        when(appUserRepository.findByUsername(username))
                .thenReturn(Optional.of(existingUser));

        assertThrows(
                IllegalArgumentException.class,
                () -> authService.registerUser(
                        username,
                        "Password123!",
                        Role.TECHNICIAN
                )
        );
    }
}