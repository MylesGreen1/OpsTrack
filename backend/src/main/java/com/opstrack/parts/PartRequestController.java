package com.opstrack.parts;

import com.opstrack.security.AppUser;
import com.opstrack.security.AppUserRepository;
import com.opstrack.technician.Technician;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/part-requests")
public class PartRequestController {
    private final PartRequestService service;
    private final AppUserRepository appUserRepository;

    public PartRequestController(PartRequestService service, AppUserRepository appUserRepository) {
        this.service = service;
        this.appUserRepository = appUserRepository;
    }

    @GetMapping
    public List<PartRequest> getAll() { return service.getAll(); }

    @GetMapping("/my-requests")
    public List<PartRequest> getMyRequests(Authentication authentication) {
        return service.getMine(requireTechnician(authentication).getId());
    }

    @GetMapping("/maintenance-task/{taskId}")
    public List<PartRequest> getByTask(@PathVariable Long taskId) { return service.getByTask(taskId); }

    @PostMapping
    public PartRequest create(@RequestParam Long maintenanceTaskId,
                              @Valid @RequestBody PartRequest request,
                              Authentication authentication) {
        return service.create(maintenanceTaskId, requireTechnician(authentication).getId(), request);
    }

    @PatchMapping("/{id}/status")
    public PartRequest updateStatus(@PathVariable Long id, @RequestParam PartRequestStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.delete(id); }

    private Technician requireTechnician(Authentication authentication) {
        AppUser user = appUserRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found."));
        if (user.getTechnician() == null) {
            throw new IllegalStateException("Authenticated user is not linked to a technician record.");
        }
        return user.getTechnician();
    }
}
