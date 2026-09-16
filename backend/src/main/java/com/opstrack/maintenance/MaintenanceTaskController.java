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

    @GetMapping
    public List<MaintenanceTask> getAllTasks() {
        return maintenanceTaskService.getAllTasks();
    }

    @PostMapping
    public MaintenanceTask createTask(@RequestBody MaintenanceTask task) {
        return maintenanceTaskService.createTask(task);
    }

    @PutMapping("/{taskId}")
    public MaintenanceTask updateTask(
            @PathVariable Long taskId,
            @RequestBody MaintenanceTask task
    ) {
        return maintenanceTaskService.updateTask(
                taskId,
                task
        );
    }

    @GetMapping("/aircraft/{aircraftId}")
    public List<MaintenanceTask> getTasksByAircraftId(@PathVariable Long aircraftId) {
        return maintenanceTaskService.getTasksByAircraftId(aircraftId);
    }

    @PatchMapping("/{taskId}/status")
    public MaintenanceTask updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam MaintenanceStatus status
    ) {
        return maintenanceTaskService.updateTaskStatus(taskId, status);
    }

    @PatchMapping("/{taskId}/technician/{technicianId}")
    public MaintenanceTask assignTechnician(
            @PathVariable Long taskId,
            @PathVariable Long technicianId
    ) {
        return maintenanceTaskService.assignTechnician(taskId, technicianId);
    }
}