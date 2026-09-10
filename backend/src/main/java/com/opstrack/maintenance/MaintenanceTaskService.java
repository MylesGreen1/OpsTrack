package com.opstrack.maintenance;

import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceTaskService {

    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final TechnicianRepository technicianRepository;

    public MaintenanceTaskService(
            MaintenanceTaskRepository maintenanceTaskRepository,
            TechnicianRepository technicianRepository
    ) {
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.technicianRepository = technicianRepository;
    }

    public MaintenanceTask createTask(MaintenanceTask task) {
        return maintenanceTaskRepository.save(task);
    }

    public List<MaintenanceTask> getTasksByAircraftId(Long aircraftId) {
        return maintenanceTaskRepository.findByAircraftId(aircraftId);
    }

    public MaintenanceTask updateTaskStatus(
            Long taskId,
            MaintenanceStatus status
    ) {
        MaintenanceTask task = maintenanceTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Maintenance task not found with id: " + taskId
                ));

        task.setStatus(status);

        return maintenanceTaskRepository.save(task);
    }

    public MaintenanceTask assignTechnician(
            Long taskId,
            Long technicianId
    ) {
        MaintenanceTask task = maintenanceTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Maintenance task not found with id: " + taskId
                ));

        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Technician not found with id: " + technicianId
                ));

        task.setTechnician(technician);

        return maintenanceTaskRepository.save(task);
    }
}