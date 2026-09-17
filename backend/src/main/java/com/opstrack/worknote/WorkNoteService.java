package com.opstrack.worknote;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.maintenance.MaintenanceTaskRepository;
import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WorkNoteService {

    private final WorkNoteRepository workNoteRepository;
    private final MaintenanceTaskRepository maintenanceTaskRepository;
    private final TechnicianRepository technicianRepository;

    public WorkNoteService(
            WorkNoteRepository workNoteRepository,
            MaintenanceTaskRepository maintenanceTaskRepository,
            TechnicianRepository technicianRepository
    ) {
        this.workNoteRepository = workNoteRepository;
        this.maintenanceTaskRepository = maintenanceTaskRepository;
        this.technicianRepository = technicianRepository;
    }

    public WorkNote createWorkNote(
            Long maintenanceTaskId,
            Long technicianId,
            String note
    ) {
        MaintenanceTask maintenanceTask =
                maintenanceTaskRepository.findById(maintenanceTaskId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Maintenance task not found with id: "
                                        + maintenanceTaskId
                        ));

        Technician technician =
                technicianRepository.findById(technicianId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Technician not found with id: "
                                        + technicianId
                        ));

        WorkNote workNote = new WorkNote(
                note,
                maintenanceTask,
                technician
        );

        return workNoteRepository.save(workNote);
    }

    public List<WorkNote> getAllWorkNotes() {
        return workNoteRepository.findAll();
    }

    public List<WorkNote> getWorkNotesByMaintenanceTaskId(
            Long maintenanceTaskId
    ) {
        return workNoteRepository.findByMaintenanceTaskId(
                maintenanceTaskId
        );
    }

    public void deleteWorkNote(Long id) {

        WorkNote workNote =
                workNoteRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Work note not found with id: " + id
                        ));

        workNoteRepository.delete(workNote);
    }

}