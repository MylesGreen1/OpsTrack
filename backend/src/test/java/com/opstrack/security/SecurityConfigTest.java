package com.opstrack.security;

import com.opstrack.inspection.InspectionService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private AppUserRepository appUserRepository;

    @MockitoBean
    private InspectionService inspectionService;

    @Test
    void shouldRejectUnauthenticatedUser() throws Exception {

        mockMvc.perform(
                        get("/api/inspections/maintenance-task/1")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectTechnicianFromInspectionEndpoint()
            throws Exception {

        mockMvc.perform(
                        get("/api/inspections/maintenance-task/1")
                                .with(
                                        user("tech1")
                                                .roles("TECHNICIAN")
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowQaInspectorToAccessInspectionEndpoint()
            throws Exception {

        mockMvc.perform(
                        get("/api/inspections/maintenance-task/1")
                                .with(
                                        user("qa1")
                                                .roles("QA_INSPECTOR")
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldAuthenticateQaInspectorWithSession()
            throws Exception {

        String username = "qa-login-test";
        String password = "Password123!";

        AppUser existingUser =
                appUserRepository
                        .findByUsername(username)
                        .orElse(null);

        if (existingUser != null) {
            appUserRepository.delete(existingUser);
        }

        authService.registerUser(
                username,
                password,
                Role.QA_INSPECTOR
        );

        MvcResult loginResult =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType("application/json")
                                        .content(
                                                """
                                                {
                                                  "username": "qa-login-test",
                                                  "password": "Password123!"
                                                }
                                                """
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        HttpSession session =
                loginResult
                        .getRequest()
                        .getSession(false);

        mockMvc.perform(
                        get("/api/inspections/maintenance-task/1")
                                .session(
                                        (org.springframework.mock.web.MockHttpSession)
                                                session
                                )
                )
                .andExpect(status().isOk());
    }
}