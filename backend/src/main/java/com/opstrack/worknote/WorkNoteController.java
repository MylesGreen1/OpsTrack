package com.opstrack.worknote;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-notes")
public class WorkNoteController {

    private final WorkNoteService workNoteService;

    public WorkNoteController(WorkNoteService workNoteService) {
        this.workNoteService = workNoteService;
    }

    @GetMapping
    public List<WorkNote> getAllWorkNotes() {
        return workNoteService.getAllWorkNotes();
    }

    @PostMapping
    public WorkNote createWorkNote(
            @RequestParam Long maintenanceTaskId,
            @RequestParam Long technicianId,
            @RequestParam String note
    ) {
        return workNoteService.createWorkNote(
                maintenanceTaskId,
                technicianId,
                note
        );
    }

    @GetMapping("/maintenance-task/{maintenanceTaskId}")
    public List<WorkNote> getWorkNotesByMaintenanceTaskId(
            @PathVariable Long maintenanceTaskId
    ) {
        return workNoteService.getWorkNotesByMaintenanceTaskId(
                maintenanceTaskId
        );
    }

    @DeleteMapping("/{id}")
    public void deleteWorkNote(
            @PathVariable Long id
    ) {
        workNoteService.deleteWorkNote(id);
    }
}