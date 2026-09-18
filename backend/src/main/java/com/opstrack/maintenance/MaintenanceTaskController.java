package com.opstrack.maintenance;

import com.opstrack.security.AppUser;
import com.opstrack.security.AppUserRepository;
import com.opstrack.technician.Technician;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-tasks")
public class MaintenanceTaskController {

    private final MaintenanceTaskService maintenanceTaskService;
    private final AppUserRepository appUserRepository;

    public MaintenanceTaskController(
            MaintenanceTaskService maintenanceTaskService,
            AppUserRepository appUserRepository
    ) {
        this.maintenanceTaskService = maintenanceTaskService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    public List<MaintenanceTask> getAllTasks() {
        return maintenanceTaskService.getAllTasks();
    }

    @GetMapping("/my-tasks")
    public List<MaintenanceTask> getMyTasks(
            Authentication authentication
    ) {

        AppUser appUser =
                appUserRepository
                        .findByUsername(
                                authentication.getName()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Authenticated user not found."
                                )
                        );

        Technician technician =
                appUser.getTechnician();

        if (technician == null) {
            throw new IllegalStateException(
                    "Authenticated user is not linked " +
                            "to a technician record."
            );
        }

        return maintenanceTaskService
                .getTasksByTechnicianId(
                        technician.getId()
                );
    }

    @PatchMapping("/my-tasks/{taskId}/status")
    public MaintenanceTask updateMyTaskStatus(
            @PathVariable Long taskId,
            @RequestParam MaintenanceStatus status,
            Authentication authentication
    ) {

        AppUser appUser =
                appUserRepository
                        .findByUsername(
                                authentication.getName()
                        )
                        .orElseThrow(
                                () -> new IllegalArgumentException(
                                        "Authenticated user not found."
                                )
                        );

        Technician technician =
                appUser.getTechnician();

        if (technician == null) {
            throw new IllegalStateException(
                    "Authenticated user is not linked " +
                            "to a technician record."
            );
        }

        return maintenanceTaskService
                .updateAssignedTaskStatus(
                        taskId,
                        technician.getId(),
                        status
                );
    }

    @PostMapping
    public MaintenanceTask createTask(
            @RequestBody MaintenanceTask task
    ) {
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
    public List<MaintenanceTask> getTasksByAircraftId(
            @PathVariable Long aircraftId
    ) {
        return maintenanceTaskService
                .getTasksByAircraftId(
                        aircraftId
                );
    }

    @PatchMapping("/{taskId}/status")
    public MaintenanceTask updateTaskStatus(
            @PathVariable Long taskId,
            @RequestParam MaintenanceStatus status
    ) {
        return maintenanceTaskService
                .updateTaskStatus(
                        taskId,
                        status
                );
    }

    @PatchMapping("/{taskId}/technician/{technicianId}")
    public MaintenanceTask assignTechnician(
            @PathVariable Long taskId,
            @PathVariable Long technicianId
    ) {
        return maintenanceTaskService
                .assignTechnician(
                        taskId,
                        technicianId
                );
    }

    @PatchMapping("/{taskId}/technician/unassign")
    public MaintenanceTask unassignTechnician(
            @PathVariable Long taskId
    ) {
        return maintenanceTaskService
                .unassignTechnician(
                        taskId
                );
    }

    @DeleteMapping("/{taskId}")
    public void deleteTask(
            @PathVariable Long taskId
    ) {
        maintenanceTaskService.deleteTask(
                taskId
        );
    }
}