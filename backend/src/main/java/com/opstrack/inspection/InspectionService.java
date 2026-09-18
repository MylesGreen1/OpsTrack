package com.opstrack.inspection;

import com.opstrack.maintenance.MaintenanceStatus;
import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.maintenance.MaintenanceTaskRepository;
import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final TechnicianRepository technicianRepository;

    public InspectionService(
            InspectionRepository inspectionRepository,
            MaintenanceTaskRepository maintenanceTaskRepository,
            TechnicianRepository technicianRepository
    ) {
        this.inspectionRepository = inspectionRepository;
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.technicianRepository = technicianRepository;
    }

    public Inspection createInspection(
            Long maintenanceTaskId,
            Long inspectorId,
            InspectionStatus status,
            String comments
    ) {
        MaintenanceTask task =
                maintenanceTaskRepository.findById(maintenanceTaskId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Maintenance task not found with id: "
                                        + maintenanceTaskId
                        ));

        Technician inspector =
                technicianRepository.findById(inspectorId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Inspector not found with id: "
                                        + inspectorId
                        ));

        Inspection inspection = new Inspection(
                status,
                comments,
                task,
                inspector
        );

        if (status == InspectionStatus.APPROVED) {
            task.setStatus(MaintenanceStatus.COMPLETED);
        }

        if (status == InspectionStatus.REJECTED) {
            task.setStatus(MaintenanceStatus.IN_PROGRESS);
        }

        maintenanceTaskRepository.save(task);

        return inspectionRepository.save(inspection);
    }

    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    public List<Inspection> getInspectionsByMaintenanceTaskId(
            Long maintenanceTaskId
    ) {
        return inspectionRepository.findByMaintenanceTaskId(
                maintenanceTaskId
        );
    }
}