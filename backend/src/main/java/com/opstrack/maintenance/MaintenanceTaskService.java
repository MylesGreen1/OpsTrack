package com.opstrack.maintenance;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;

    public MaintenanceTaskService(MaintenanceTaskRepository maintenanceTaskRepository) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
    }

    public MaintenanceTask createTask(MaintenanceTask task) {
        return maintenanceTaskRepository.save(task);
    }

    public List<MaintenanceTask> getTasksByAircraftId(Long aircraftId) {
        return maintenanceTaskRepository.findByAircraftId(aircraftId);
    }

    public MaintenanceTask updateTaskStatus(Long taskId, MaintenanceStatus status) {
        MaintenanceTask task = maintenanceTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Maintenance task not found with id: " + taskId
                ));

        task.setStatus(status);

        return maintenanceTaskRepository.save(task);
    }
}