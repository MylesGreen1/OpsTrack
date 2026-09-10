package com.opstrack.inspection;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    public Inspection createInspection(
            @RequestParam Long maintenanceTaskId,
            @RequestParam Long inspectorId,
            @RequestParam InspectionStatus status,
            @RequestParam String comments
    ) {
        return inspectionService.createInspection(
                maintenanceTaskId,
                inspectorId,
                status,
                comments
        );
    }

    @GetMapping("/maintenance-task/{maintenanceTaskId}")
    public List<Inspection> getInspectionsByMaintenanceTaskId(
            @PathVariable Long maintenanceTaskId
    ) {
        return inspectionService.getInspectionsByMaintenanceTaskId(
                maintenanceTaskId
        );
    }
}