package com.opstrack.aircraft;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AircraftController.class)
public class AircraftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AircraftService aircraftService;

    @Test
    void shouldReturnAllAircraft() throws Exception {

        Aircraft aircraftOne = new Aircraft(
                "AF-001",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 1",
                "Ready for operations"
        );

        Aircraft aircraftTwo = new Aircraft(
                "AF-002",
                "C-130",
                AircraftStatus.IN_MAINTENANCE,
                "Hangar 2",
                "Scheduled maintenance"
        );

        when(aircraftService.getAllAircraft())
                .thenReturn(
                        List.of(
                                aircraftOne,
                                aircraftTwo
                        )
                );

        mockMvc.perform(
                        get("/api/aircraft")
                                .with(
                                        user("technician")
                                                .roles("TECHNICIAN")
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$[0].tailNumber")
                                .value("AF-001")
                )
                .andExpect(
                        jsonPath("$[0].aircraftType")
                                .value("F-16")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("MISSION_CAPABLE")
                )
                .andExpect(
                        jsonPath("$[1].tailNumber")
                                .value("AF-002")
                );
    }

    @Test
    void shouldReturnAircraftById() throws Exception {

        Aircraft aircraft = new Aircraft(
                "AF-003",
                "F-22",
                AircraftStatus.MISSION_CAPABLE,
                "Flight Line",
                "Operational"
        );

        when(aircraftService.getAircraftById(1L))
                .thenReturn(aircraft);

        mockMvc.perform(
                        get("/api/aircraft/1")
                                .with(
                                        user("technician")
                                                .roles("TECHNICIAN")
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.tailNumber")
                                .value("AF-003")
                )
                .andExpect(
                        jsonPath("$.location")
                                .value("Flight Line")
                );
    }

    @Test
    void shouldCreateAircraft() throws Exception {

        Aircraft aircraft = new Aircraft(
                "AF-004",
                "F-35",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 3",
                "New aircraft record"
        );

        when(
                aircraftService.createAircraft(
                        any(Aircraft.class)
                )
        ).thenReturn(aircraft);

        mockMvc.perform(
                        post("/api/aircraft")
                                .with(
                                        user("supervisor")
                                                .roles("SUPERVISOR")
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(
                                                aircraft
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.tailNumber")
                                .value("AF-004")
                )
                .andExpect(
                        jsonPath("$.aircraftType")
                                .value("F-35")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("MISSION_CAPABLE")
                );
    }

    @Test
    void shouldUpdateAircraft() throws Exception {

        Aircraft updatedAircraft = new Aircraft(
                "AF-005",
                "C-17",
                AircraftStatus.PARTIALLY_MISSION_CAPABLE,
                "Ramp",
                "Updated record"
        );

        when(
                aircraftService.updateAircraft(
                        any(Long.class),
                        any(Aircraft.class)
                )
        ).thenReturn(updatedAircraft);

        mockMvc.perform(
                        put("/api/aircraft/1")
                                .with(
                                        user("supervisor")
                                                .roles("SUPERVISOR")
                                )
                                .contentType("application/json")
                                .content(
                                        objectMapper.writeValueAsString(
                                                updatedAircraft
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.tailNumber")
                                .value("AF-005")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value(
                                        "PARTIALLY_MISSION_CAPABLE"
                                )
                );
    }

    @Test
    void shouldUpdateAircraftStatus() throws Exception {

        Aircraft aircraft = new Aircraft(
                "AF-006",
                "F-15",
                AircraftStatus.IN_MAINTENANCE,
                "Hangar 5",
                "Maintenance in progress"
        );

        when(
                aircraftService.updateAircraftStatus(
                        1L,
                        AircraftStatus.IN_MAINTENANCE
                )
        ).thenReturn(aircraft);

        mockMvc.perform(
                        patch(
                                "/api/aircraft/1/status"
                        )
                                .with(
                                        user("technician")
                                                .roles("TECHNICIAN")
                                )
                                .param(
                                        "status",
                                        "IN_MAINTENANCE"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("IN_MAINTENANCE")
                );
    }

    @Test
    void shouldDeleteAircraft() throws Exception {

        mockMvc.perform(
                        delete("/api/aircraft/1")
                                .with(
                                        user("admin")
                                                .roles("ADMIN")
                                )
                )
                .andExpect(status().isOk());
    }

}