package com.opstrack.maintenance;

import org.springframework.http.MediaType;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(MaintenanceTaskController.class)
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
                .andExpect(jsonPath("$.status").value("OPEN"));;

    }

}