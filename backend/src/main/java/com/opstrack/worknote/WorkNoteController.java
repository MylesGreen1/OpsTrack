package com.opstrack.worknote;

import com.opstrack.security.AppUser;
import com.opstrack.security.AppUserRepository;
import com.opstrack.technician.Technician;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-notes")
public class WorkNoteController {

    private final WorkNoteService workNoteService;
    private final AppUserRepository appUserRepository;

    public WorkNoteController(
            WorkNoteService workNoteService,
            AppUserRepository appUserRepository
    ) {
        this.workNoteService = workNoteService;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    public List<WorkNote> getAllWorkNotes() {
        return workNoteService.getAllWorkNotes();
    }

    @PostMapping
    public WorkNote createWorkNote(
            @RequestParam Long maintenanceTaskId,
            @RequestParam String note,
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

        return workNoteService.createWorkNote(
                maintenanceTaskId,
                technician.getId(),
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