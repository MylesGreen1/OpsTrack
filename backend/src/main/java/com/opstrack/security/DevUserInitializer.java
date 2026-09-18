package com.opstrack.security;

import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class DevUserInitializer {

    @Bean
    public CommandLineRunner createDevUsers(
            AuthService authService,
            AppUserRepository appUserRepository,
            TechnicianRepository technicianRepository
    ) {
        return args -> {

            // -------------------------
            // Admin development user
            // -------------------------

            String adminUsername = "admin";
            String adminPassword =
                    System.getenv("OPSTRACK_ADMIN_PASSWORD");

            if (adminPassword == null ||
                    adminPassword.isBlank()) {

                System.out.println(
                        "OPSTRACK_ADMIN_PASSWORD is not set. " +
                                "Admin development user was not created."
                );

            } else {

                AppUser adminUser =
                        appUserRepository
                                .findByUsername(adminUsername)
                                .orElseGet(() ->
                                        authService.registerUser(
                                                adminUsername,
                                                adminPassword,
                                                Role.ADMIN
                                        )
                                );

                boolean adminUpdated = false;

                if (adminUser.getRole() != Role.ADMIN) {

                    adminUser.setRole(Role.ADMIN);
                    adminUpdated = true;
                }

                if (adminUser.getTechnician() != null) {

                    adminUser.setTechnician(null);
                    adminUpdated = true;
                }

                if (adminUpdated) {

                    appUserRepository.save(adminUser);

                    System.out.println(
                            "Admin development user updated: " +
                                    adminUsername
                    );
                }
            }

            // -------------------------
            // QA Inspector development user
            // -------------------------

            String qaUsername = "qa.inspector";
            String qaPassword =
                    System.getenv("OPSTRACK_QA_PASSWORD");

            if (qaPassword == null ||
                    qaPassword.isBlank()) {

                System.out.println(
                        "OPSTRACK_QA_PASSWORD is not set. " +
                                "QA development user was not created."
                );

            } else if (appUserRepository
                    .findByUsername(qaUsername)
                    .isEmpty()) {

                authService.registerUser(
                        qaUsername,
                        qaPassword,
                        Role.QA_INSPECTOR
                );

                System.out.println(
                        "QA development user created: " +
                                qaUsername
                );
            }

            // -------------------------
            // Technician development user
            // -------------------------

            String technicianUsername = "technician";
            String technicianPassword =
                    System.getenv(
                            "OPSTRACK_TECHNICIAN_PASSWORD"
                    );

            if (technicianPassword == null ||
                    technicianPassword.isBlank()) {

                System.out.println(
                        "OPSTRACK_TECHNICIAN_PASSWORD is not set. " +
                                "Technician development user was not created."
                );

            } else {

                Technician technician =
                        technicianRepository
                                .findByEmployeeNumber(
                                        "DEV-TECH-001"
                                )
                                .orElseGet(() -> {

                                    Technician newTechnician =
                                            new Technician(
                                                    "Development",
                                                    "Technician",
                                                    "DEV-TECH-001",
                                                    "Aircraft Maintenance",
                                                    true
                                            );

                                    return technicianRepository.save(
                                            newTechnician
                                    );
                                });

                AppUser technicianUser =
                        appUserRepository
                                .findByUsername(
                                        technicianUsername
                                )
                                .orElseGet(() ->
                                        authService.registerUser(
                                                technicianUsername,
                                                technicianPassword,
                                                Role.TECHNICIAN
                                        )
                                );

                if (technicianUser.getTechnician() == null) {

                    technicianUser.setTechnician(
                            technician
                    );

                    appUserRepository.save(
                            technicianUser
                    );

                    System.out.println(
                            "Technician development user linked " +
                                    "to technician record."
                    );
                }
            }
        };
    }
}