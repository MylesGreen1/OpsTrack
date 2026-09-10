package com.opstrack.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AppUserRepositoryTest {

    @Autowired
    private AppUserRepository appUserRepository;

    @Test
    void shouldSaveAppUser() {
        AppUser appUser = new AppUser(
                "tech1",
                "hashed-password-placeholder",
                Role.TECHNICIAN,
                true
        );

        AppUser savedUser = appUserRepository.save(appUser);

        assertNotNull(savedUser.getId());
        assertEquals("tech1", savedUser.getUsername());
        assertEquals(Role.TECHNICIAN, savedUser.getRole());
    }

    @Test
    void shouldFindAppUserByUsername() {
        AppUser appUser = new AppUser(
                "qa1",
                "hashed-password-placeholder",
                Role.QA_INSPECTOR,
                true
        );

        appUserRepository.save(appUser);

        AppUser foundUser = appUserRepository
                .findByUsername("qa1")
                .orElseThrow();

        assertEquals("qa1", foundUser.getUsername());
        assertEquals(Role.QA_INSPECTOR, foundUser.getRole());
        assertEquals(true, foundUser.isEnabled());
    }
}