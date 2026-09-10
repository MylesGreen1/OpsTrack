package com.opstrack.maintenance;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-tasks")
public class MaintenanceTaskController {

    private final MaintenanceTaskService maintenanceTaskService;

    public MaintenanceTaskController(MaintenanceTaskService maintenanceTaskService) {
        this.maintenanceTaskService = maintenanceTaskService;
    }

    @PostMapping
    public MaintenanceTask createTask(@RequestBody MaintenanceTask task) {
        return maintenanceTaskService.createTask(task);
    }

    @GetMapping("/aircraft/{aircraftId}")
    public List<MaintenanceTask> getTasksByAircraftId(@PathVariable Long aircraftId) {
        return maintenanceTaskService.getTasksByAircraftId(aircraftId);

    }

}