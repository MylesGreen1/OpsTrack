package com.opstrack.security;

import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
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

    @Autowired
    private TechnicianRepository technicianRepository;

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

    @Test
    void shouldFindAppUserByTechnicianId() {

        Technician technician = new Technician(
                "Alex",
                "Morgan",
                "TECH-100",
                "Avionics",
                true
        );

        Technician savedTechnician =
                technicianRepository.save(technician);

        AppUser appUser = new AppUser(
                "alex.morgan",
                "hashed-password-placeholder",
                Role.TECHNICIAN,
                true
        );

        appUser.setTechnician(savedTechnician);

        appUserRepository.save(appUser);

        AppUser foundUser =
                appUserRepository
                        .findByTechnicianId(
                                savedTechnician.getId()
                        )
                        .orElseThrow();

        assertEquals(
                "alex.morgan",
                foundUser.getUsername()
        );

        assertNotNull(
                foundUser.getTechnician()
        );

        assertEquals(
                savedTechnician.getId(),
                foundUser.getTechnician().getId()
        );
    }
}
