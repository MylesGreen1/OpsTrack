package com.opstrack.security;

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
            AppUserRepository appUserRepository
    ) {
        return args -> {

            // -------------------------
            // QA Inspector development user
            // -------------------------

            String qaUsername = "qa.inspector";
            String qaPassword =
                    System.getenv("OPSTRACK_QA_PASSWORD");

            if (qaPassword == null || qaPassword.isBlank()) {

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
                    System.getenv("OPSTRACK_TECHNICIAN_PASSWORD");

            if (technicianPassword == null ||
                    technicianPassword.isBlank()) {

                System.out.println(
                        "OPSTRACK_TECHNICIAN_PASSWORD is not set. " +
                                "Technician development user was not created."
                );

            } else if (appUserRepository
                    .findByUsername(technicianUsername)
                    .isEmpty()) {

                authService.registerUser(
                        technicianUsername,
                        technicianPassword,
                        Role.TECHNICIAN
                );

                System.out.println(
                        "Technician development user created: " +
                                technicianUsername
                );
            }
        };
    }
}