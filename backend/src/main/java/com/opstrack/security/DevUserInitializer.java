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

            String qaUsername = "qa.inspector";
            String qaPassword =
                    System.getenv("OPSTRACK_QA_PASSWORD");

            if (qaPassword == null || qaPassword.isBlank()) {
                System.out.println(
                        "OPSTRACK_QA_PASSWORD is not set. " +
                                "QA development user was not created."
                );
                return;
            }

            if (appUserRepository
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
        };
    }
}