package com.opstrack.parts;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.maintenance.MaintenanceTaskRepository;
import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PartRequestService {
    private final PartRequestRepository repository;
    private final MaintenanceTaskRepository taskRepository;
    private final TechnicianRepository technicianRepository;

    public PartRequestService(PartRequestRepository repository,
                              MaintenanceTaskRepository taskRepository,
                              TechnicianRepository technicianRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
        this.technicianRepository = technicianRepository;
    }

    public List<PartRequest> getAll() { return repository.findAll(); }
    public List<PartRequest> getByTask(Long taskId) { return repository.findByMaintenanceTaskId(taskId); }
    public List<PartRequest> getMine(Long technicianId) { return repository.findByRequestedById(technicianId); }

    public PartRequest create(Long taskId, Long technicianId, PartRequest request) {
        MaintenanceTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Maintenance task not found with id: " + taskId));
        Technician technician = technicianRepository.findById(technicianId)
                .orElseThrow(() -> new IllegalArgumentException("Technician not found with id: " + technicianId));
        request.setMaintenanceTask(task);
        request.setRequestedBy(technician);
        request.setStatus(PartRequestStatus.REQUESTED);
        return repository.save(request);
    }

    public PartRequest updateStatus(Long id, PartRequestStatus status) {
        PartRequest request = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part request not found with id: " + id));
        request.setStatus(status);
        return repository.save(request);
    }

    public void delete(Long id) {
        PartRequest request = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Part request not found with id: " + id));
        repository.delete(request);
    }
}
