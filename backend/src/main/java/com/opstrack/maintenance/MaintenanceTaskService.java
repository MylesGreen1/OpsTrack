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

    public List<MaintenanceTask> getAllTasks() {
        return maintenanceTaskRepository.findAll();
    }

    public List<MaintenanceTask> getTasksByAircraftId(Long aircraftId) {
        return maintenanceTaskRepository.findByAircraftId(aircraftId);
    }


    public List<MaintenanceTask> getTasksByTechnicianId(
            Long technicianId
    ) {
        return maintenanceTaskRepository.findByTechnicianId(
                technicianId
        );
    }

    public MaintenanceTask updateTask(
            Long taskId,
            MaintenanceTask updatedTask
    ) {
        MaintenanceTask existingTask =
                maintenanceTaskRepository.findById(taskId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Maintenance task not found with id: " + taskId
                        ));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setPriority(updatedTask.getPriority());
        existingTask.setStatus(updatedTask.getStatus());
        existingTask.setAircraft(updatedTask.getAircraft());

        return maintenanceTaskRepository.save(existingTask);
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

    public MaintenanceTask unassignTechnician(
            Long taskId
    ) {
        MaintenanceTask task = maintenanceTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Maintenance task not found with id: " + taskId
                ));

        task.setTechnician(null);

        return maintenanceTaskRepository.save(task);
    }

    public void deleteTask(Long taskId) {

        MaintenanceTask task =
                maintenanceTaskRepository.findById(taskId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Maintenance task not found with id: " + taskId
                        ));

        maintenanceTaskRepository.delete(task);
    }
}