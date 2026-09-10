package com.opstrack.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void shouldRegisterUserWithoutAuthentication() throws Exception {
        AppUser appUser = new AppUser(
                "tech3",
                "encoded-password",
                Role.TECHNICIAN,
                true
        );

        when(
                authService.registerUser(
                        "tech3",
                        "Password123!",
                        Role.TECHNICIAN
                )
        ).thenReturn(appUser);

        mockMvc.perform(
                        post("/api/auth/register")
                                .param("username", "tech3")
                                .param("password", "Password123!")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.username")
                                .value("tech3")
                )
                .andExpect(
                        jsonPath("$.role")
                                .value("TECHNICIAN")
                )
                .andExpect(
                        jsonPath("$.enabled")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.password")
                                .doesNotExist()
                );
    }
}