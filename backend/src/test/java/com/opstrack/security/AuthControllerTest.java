package com.opstrack.security;

import com.opstrack.technician.Technician;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
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

    @MockitoBean
    private AppUserRepository appUserRepository;

    @MockitoBean
    private AuthenticationManager authenticationManager;

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

    @Test
    void shouldReturnCurrentUserWithTechnicianId() {

        Technician technician =
                mock(Technician.class);

        when(
                technician.getId()
        ).thenReturn(1L);

        AppUser appUser = new AppUser(
                "technician",
                "encoded-password",
                Role.TECHNICIAN,
                true
        );

        appUser.setTechnician(technician);

        Authentication authentication =
                mock(Authentication.class);

        when(
                authentication.getName()
        ).thenReturn("technician");

        when(
                appUserRepository.findByUsername(
                        "technician"
                )
        ).thenReturn(
                Optional.of(appUser)
        );

        AuthController authController =
                new AuthController(
                        authService,
                        appUserRepository,
                        authenticationManager
                );

        CurrentUserResponse response =
                authController.getCurrentUser(
                        authentication
                );

        assertEquals(
                "technician",
                response.username()
        );

        assertEquals(
                Role.TECHNICIAN,
                response.role()
        );

        assertEquals(
                true,
                response.enabled()
        );

        assertEquals(
                1L,
                response.technicianId()
        );
    }

    @Test
    void shouldLoginAndCreateSession() throws Exception {

        AppUser adminUser = new AppUser(
                "admin",
                "encoded-password",
                Role.ADMIN,
                true
        );

        Authentication authentication =
                mock(Authentication.class);

        when(
                authentication.getName()
        ).thenReturn("admin");

        when(
                authenticationManager.authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                )
        ).thenReturn(authentication);

        when(
                appUserRepository.findByUsername(
                        "admin"
                )
        ).thenReturn(
                Optional.of(adminUser)
        );

        HttpSession session =
                mockMvc.perform(
                                post("/api/auth/login")
                                        .contentType(
                                                "application/json"
                                        )
                                        .content(
                                                """
                                                {
                                                  "username": "admin",
                                                  "password": "Password123!"
                                                }
                                                """
                                        )
                        )
                        .andExpect(status().isOk())
                        .andExpect(
                                jsonPath("$.username")
                                        .value("admin")
                        )
                        .andExpect(
                                jsonPath("$.role")
                                        .value("ADMIN")
                        )
                        .andExpect(
                                jsonPath("$.enabled")
                                        .value(true)
                        )
                        .andExpect(
                                jsonPath("$.technicianId")
                                        .doesNotExist()
                        )
                        .andReturn()
                        .getRequest()
                        .getSession(false);

        assertNotNull(session);

        Object securityContext =
                session.getAttribute(
                        HttpSessionSecurityContextRepository
                                .SPRING_SECURITY_CONTEXT_KEY
                );

        assertNotNull(securityContext);

        SecurityContext context =
                (SecurityContext) securityContext;

        assertEquals(
                authentication,
                context.getAuthentication()
        );
    }

    @Test
    void shouldLogoutAndInvalidateSession() throws Exception {

        MockHttpSession session =
                new MockHttpSession();

        session.setAttribute(
                HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY,
                mock(SecurityContext.class)
        );

        mockMvc.perform(
                        post("/api/auth/logout")
                                .session(session)
                )
                .andExpect(status().isOk());

        assertThrows(
                IllegalStateException.class,
                () -> session.getAttribute(
                        HttpSessionSecurityContextRepository
                                .SPRING_SECURITY_CONTEXT_KEY
                )
        );
    }
}