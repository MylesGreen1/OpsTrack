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
}
