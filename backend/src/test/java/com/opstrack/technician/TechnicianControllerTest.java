package com.opstrack.technician;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TechnicianController.class)
@WithMockUser(roles = "SUPERVISOR")
public class TechnicianControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TechnicianService technicianService;

    @Test
    void shouldGetAllTechnicians() throws Exception {
        Technician technician1 = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        Technician technician2 = new Technician(
                "Jordan",
                "Lee",
                "TECH-002",
                "Hydraulics",
                true
        );

        when(technicianService.getAllTechnicians())
                .thenReturn(
                        List.of(
                                technician1,
                                technician2
                        )
                );

        mockMvc.perform(
                        get("/api/technicians")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].employeeNumber")
                                .value("TECH-001")
                )
                .andExpect(
                        jsonPath("$[1].employeeNumber")
                                .value("TECH-002")
                );
    }

    @Test
    void shouldGetTechnicianById() throws Exception {
        Long technicianId = 1L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        technician.setId(technicianId);

        when(
                technicianService.getTechnicianById(
                        technicianId
                )
        ).thenReturn(technician);

        mockMvc.perform(
                        get("/api/technicians/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Alex")
                )
                .andExpect(
                        jsonPath("$.employeeNumber")
                                .value("TECH-001")
                );
    }

    @Test
    void shouldCreateTechnician() throws Exception {
        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        when(
                technicianService.createTechnician(
                        any(Technician.class)
                )
        ).thenReturn(technician);

        mockMvc.perform(
                        post("/api/technicians")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                technician
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Alex")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Carter")
                )
                .andExpect(
                        jsonPath("$.employeeNumber")
                                .value("TECH-001")
                )
                .andExpect(
                        jsonPath("$.specialty")
                                .value("Avionics")
                )
                .andExpect(
                        jsonPath("$.active")
                                .value(true)
                );
    }

    @Test
    void shouldUpdateTechnician() throws Exception {
        Long technicianId = 1L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Hydraulics",
                true
        );

        when(
                technicianService.updateTechnician(
                        any(Long.class),
                        any(Technician.class)
                )
        ).thenReturn(technician);

        mockMvc.perform(
                        put("/api/technicians/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                technician
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.specialty")
                                .value("Hydraulics")
                );
    }

    @Test
    void shouldUpdateTechnicianActiveStatus()
            throws Exception {

        Long technicianId = 1L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                false
        );

        when(
                technicianService
                        .updateTechnicianActiveStatus(
                                technicianId,
                                false
                        )
        ).thenReturn(technician);

        mockMvc.perform(
                        patch(
                                "/api/technicians/1/active"
                        )
                                .param(
                                        "active",
                                        "false"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.active")
                                .value(false)
                );
    }

    @Test
    void shouldDeleteTechnician() throws Exception {
        Long technicianId = 1L;

        doNothing()
                .when(technicianService)
                .deleteTechnician(technicianId);

        mockMvc.perform(
                        delete("/api/technicians/1")
                )
                .andExpect(status().isOk());
    }
}