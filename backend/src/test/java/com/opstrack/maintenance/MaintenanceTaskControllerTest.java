package com.opstrack.maintenance;

import com.opstrack.technician.Technician;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(MaintenanceTaskController.class)
@WithMockUser(roles = "SUPERVISOR")
public class MaintenanceTaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MaintenanceTaskService maintenanceTaskService;

    @Test
    void shouldGetTasksByAircraftId() throws Exception {
        Long aircraftId = 1L;

        MaintenanceTask task1 = new MaintenanceTask();
        MaintenanceTask task2 = new MaintenanceTask();

        List<MaintenanceTask> tasks = List.of(task1, task2);

        when(maintenanceTaskService.getTasksByAircraftId(aircraftId))
                .thenReturn(tasks);

        mockMvc.perform(get("/api/maintenance-tasks/aircraft/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldCreateMaintenanceTask() throws Exception {
        MaintenanceTask task = new MaintenanceTask();
        task.setTitle("Hydraulic inspection");
        task.setDescription("Inspect hydraulic system");
        task.setStatus(MaintenanceStatus.OPEN);
        task.setPriority(MaintenancePriority.HIGH);

        when(maintenanceTaskService.createTask(any(MaintenanceTask.class)))
                .thenReturn(task);

        mockMvc.perform(post("/api/maintenance-tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Hydraulic inspection"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldUpdateMaintenanceTaskStatus() throws Exception {
        Long taskId = 1L;

        MaintenanceTask task = new MaintenanceTask();
        task.setStatus(MaintenanceStatus.IN_PROGRESS);

        when(maintenanceTaskService.updateTaskStatus(
                taskId,
                MaintenanceStatus.IN_PROGRESS
        )).thenReturn(task);

        mockMvc.perform(patch("/api/maintenance-tasks/1/status")
                        .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldAssignTechnicianToMaintenanceTask() throws Exception {
        Long taskId = 1L;
        Long technicianId = 2L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-002",
                "Avionics",
                true
        );

        MaintenanceTask task = new MaintenanceTask();
        task.setTechnician(technician);

        when(maintenanceTaskService.assignTechnician(taskId, technicianId))
                .thenReturn(task);

        mockMvc.perform(
                        patch("/api/maintenance-tasks/1/technician/2")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.technician.employeeNumber")
                                .value("TECH-002")
                );
    }
}