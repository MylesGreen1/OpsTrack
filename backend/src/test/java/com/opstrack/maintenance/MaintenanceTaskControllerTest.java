package com.opstrack.maintenance;

import com.opstrack.technician.Technician;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
    void shouldGetAllMaintenanceTasks() throws Exception {
        MaintenanceTask task1 = new MaintenanceTask();
        task1.setTitle("Hydraulic inspection");
        task1.setStatus(MaintenanceStatus.OPEN);

        MaintenanceTask task2 = new MaintenanceTask();
        task2.setTitle("Engine inspection");
        task2.setStatus(MaintenanceStatus.IN_PROGRESS);

        List<MaintenanceTask> tasks =
                List.of(task1, task2);

        when(maintenanceTaskService.getAllTasks())
                .thenReturn(tasks);

        mockMvc.perform(
                        get("/api/maintenance-tasks")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].title")
                                .value("Hydraulic inspection")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("OPEN")
                )
                .andExpect(
                        jsonPath("$[1].title")
                                .value("Engine inspection")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("IN_PROGRESS")
                );
    }

    @Test
    void shouldGetTasksByAircraftId() throws Exception {
        Long aircraftId = 1L;

        MaintenanceTask task1 = new MaintenanceTask();
        MaintenanceTask task2 = new MaintenanceTask();

        List<MaintenanceTask> tasks =
                List.of(task1, task2);

        when(
                maintenanceTaskService
                        .getTasksByAircraftId(aircraftId)
        ).thenReturn(tasks);

        mockMvc.perform(
                        get("/api/maintenance-tasks/aircraft/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldCreateMaintenanceTask() throws Exception {
        MaintenanceTask task =
                new MaintenanceTask();

        task.setTitle("Hydraulic inspection");
        task.setDescription("Inspect hydraulic system");
        task.setStatus(MaintenanceStatus.OPEN);
        task.setPriority(MaintenancePriority.HIGH);

        when(
                maintenanceTaskService
                        .createTask(any(MaintenanceTask.class))
        ).thenReturn(task);

        mockMvc.perform(
                        post("/api/maintenance-tasks")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper
                                                .writeValueAsString(task)
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.title")
                                .value("Hydraulic inspection")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("OPEN")
                );
    }

    @Test
    void shouldUpdateMaintenanceTask() throws Exception {
        Long taskId = 1L;

        MaintenanceTask requestTask =
                new MaintenanceTask();

        requestTask.setTitle(
                "Updated hydraulic inspection"
        );

        requestTask.setDescription(
                "Inspect hydraulic lines and connections."
        );

        requestTask.setPriority(
                MaintenancePriority.HIGH
        );

        requestTask.setStatus(
                MaintenanceStatus.IN_PROGRESS
        );

        MaintenanceTask updatedTask =
                new MaintenanceTask();

        updatedTask.setTitle(
                "Updated hydraulic inspection"
        );

        updatedTask.setDescription(
                "Inspect hydraulic lines and connections."
        );

        updatedTask.setPriority(
                MaintenancePriority.HIGH
        );

        updatedTask.setStatus(
                MaintenanceStatus.IN_PROGRESS
        );

        when(
                maintenanceTaskService.updateTask(
                        org.mockito.ArgumentMatchers.eq(taskId),
                        any(MaintenanceTask.class)
                )
        ).thenReturn(updatedTask);

        mockMvc.perform(
                        put("/api/maintenance-tasks/1")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        objectMapper.writeValueAsString(
                                                requestTask
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.title")
                                .value(
                                        "Updated hydraulic inspection"
                                )
                )
                .andExpect(
                        jsonPath("$.description")
                                .value(
                                        "Inspect hydraulic lines and connections."
                                )
                )
                .andExpect(
                        jsonPath("$.priority")
                                .value("HIGH")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("IN_PROGRESS")
                );
    }

    @Test
    void shouldUpdateMaintenanceTaskStatus()
            throws Exception {

        Long taskId = 1L;

        MaintenanceTask task =
                new MaintenanceTask();

        task.setStatus(
                MaintenanceStatus.IN_PROGRESS
        );

        when(
                maintenanceTaskService
                        .updateTaskStatus(
                                taskId,
                                MaintenanceStatus.IN_PROGRESS
                        )
        ).thenReturn(task);

        mockMvc.perform(
                        patch(
                                "/api/maintenance-tasks/1/status"
                        )
                                .param(
                                        "status",
                                        "IN_PROGRESS"
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.status")
                                .value("IN_PROGRESS")
                );
    }

    @Test
    void shouldAssignTechnicianToMaintenanceTask()
            throws Exception {

        Long taskId = 1L;
        Long technicianId = 2L;

        Technician technician =
                new Technician(
                        "Alex",
                        "Carter",
                        "TECH-002",
                        "Avionics",
                        true
                );

        MaintenanceTask task =
                new MaintenanceTask();

        task.setTechnician(technician);

        when(
                maintenanceTaskService
                        .assignTechnician(
                                taskId,
                                technicianId
                        )
        ).thenReturn(task);

        mockMvc.perform(
                        patch(
                                "/api/maintenance-tasks/1/technician/2"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath(
                                "$.technician.employeeNumber"
                        ).value("TECH-002")
                );
    }

    @Test
    void shouldDeleteMaintenanceTask()
            throws Exception {

        Long taskId = 1L;

        mockMvc.perform(
                        delete(
                                "/api/maintenance-tasks/1"
                        )
                )
                .andExpect(
                        status().isOk()
                );

        verify(
                maintenanceTaskService
        ).deleteTask(taskId);
    }
}