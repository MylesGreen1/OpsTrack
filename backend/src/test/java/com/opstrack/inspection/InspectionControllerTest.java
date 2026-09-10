package com.opstrack.inspection;

import com.opstrack.maintenance.MaintenanceStatus;
import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.technician.Technician;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InspectionController.class)
@WithMockUser(roles = "QA_INSPECTOR")
public class InspectionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InspectionService inspectionService;

    @Test
    void shouldCreateApprovedInspection() throws Exception {
        Long maintenanceTaskId = 1L;
        Long inspectorId = 2L;

        Technician inspector = new Technician(
                "Morgan",
                "Reed",
                "QA-005",
                "Quality Assurance",
                true
        );

        MaintenanceTask task = new MaintenanceTask();
        task.setStatus(MaintenanceStatus.COMPLETED);

        Inspection inspection = new Inspection(
                InspectionStatus.APPROVED,
                "Inspection passed.",
                task,
                inspector
        );

        when(
                inspectionService.createInspection(
                        maintenanceTaskId,
                        inspectorId,
                        InspectionStatus.APPROVED,
                        "Inspection passed."
                )
        ).thenReturn(inspection);

        mockMvc.perform(
                        post("/api/inspections")
                                .param(
                                        "maintenanceTaskId",
                                        maintenanceTaskId.toString()
                                )
                                .param(
                                        "inspectorId",
                                        inspectorId.toString()
                                )
                                .param("status", "APPROVED")
                                .param(
                                        "comments",
                                        "Inspection passed."
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("APPROVED")
                )
                .andExpect(
                        jsonPath("$.comments")
                                .value("Inspection passed.")
                )
                .andExpect(
                        jsonPath("$.inspector.employeeNumber")
                                .value("QA-005")
                );
    }

    @Test
    void shouldCreateRejectedInspection() throws Exception {
        Long maintenanceTaskId = 1L;
        Long inspectorId = 2L;

        Technician inspector = new Technician(
                "Taylor",
                "Brooks",
                "QA-006",
                "Quality Assurance",
                true
        );

        MaintenanceTask task = new MaintenanceTask();
        task.setStatus(MaintenanceStatus.IN_PROGRESS);

        Inspection inspection = new Inspection(
                InspectionStatus.REJECTED,
                "Loose connector found.",
                task,
                inspector
        );

        when(
                inspectionService.createInspection(
                        maintenanceTaskId,
                        inspectorId,
                        InspectionStatus.REJECTED,
                        "Loose connector found."
                )
        ).thenReturn(inspection);

        mockMvc.perform(
                        post("/api/inspections")
                                .param(
                                        "maintenanceTaskId",
                                        maintenanceTaskId.toString()
                                )
                                .param(
                                        "inspectorId",
                                        inspectorId.toString()
                                )
                                .param("status", "REJECTED")
                                .param(
                                        "comments",
                                        "Loose connector found."
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("REJECTED")
                )
                .andExpect(
                        jsonPath("$.comments")
                                .value("Loose connector found.")
                );
    }

    @Test
    void shouldGetInspectionsByMaintenanceTaskId() throws Exception {
        Long maintenanceTaskId = 1L;

        Inspection inspection1 = new Inspection();
        inspection1.setStatus(InspectionStatus.REJECTED);

        Inspection inspection2 = new Inspection();
        inspection2.setStatus(InspectionStatus.APPROVED);

        when(
                inspectionService.getInspectionsByMaintenanceTaskId(
                        maintenanceTaskId
                )
        ).thenReturn(List.of(inspection1, inspection2));

        mockMvc.perform(
                        get(
                                "/api/inspections/maintenance-task/1"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("REJECTED")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("APPROVED")
                );
    }
}