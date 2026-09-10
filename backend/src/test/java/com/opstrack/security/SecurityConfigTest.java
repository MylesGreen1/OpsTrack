package com.opstrack.security;

import com.opstrack.inspection.InspectionController;
import com.opstrack.inspection.InspectionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InspectionService inspectionService;

    @Test
    void shouldRejectUnauthenticatedUser() throws Exception {
        mockMvc.perform(
                        get("/api/inspections/maintenance-task/1")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectTechnicianFromInspectionEndpoint() throws Exception {
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
}