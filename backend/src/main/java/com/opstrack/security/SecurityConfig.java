package com.opstrack.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/api/inspections/**")
                        .hasAnyRole("QA_INSPECTOR", "ADMIN")

                        .requestMatchers("/api/work-notes/**")
                        .hasAnyRole(
                                "TECHNICIAN",
                                "SUPERVISOR",
                                "ADMIN"
                        )

                        .requestMatchers("/api/maintenance-tasks/**")
                        .hasAnyRole(
                                "TECHNICIAN",
                                "SUPERVISOR",
                                "QA_INSPECTOR",
                                "ADMIN"
                        )

                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(basic -> {
                });

        return http.build();
    }
}