package com.opstrack.inspection;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.technician.Technician;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inspections")
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InspectionStatus status;

    private String comments;

    private LocalDateTime inspectedAt;

    @ManyToOne
    @JoinColumn(name = "maintenance_task_id")
    private MaintenanceTask maintenanceTask;

    @ManyToOne
    @JoinColumn(name = "inspector_id")
    private Technician inspector;

    public Inspection() {
    }

    public Inspection(
            InspectionStatus status,
            String comments,
            MaintenanceTask maintenanceTask,
            Technician inspector
    ) {
        this.status = status;
        this.comments = comments;
        this.maintenanceTask = maintenanceTask;
        this.inspector = inspector;
        this.inspectedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public InspectionStatus getStatus() {
        return status;
    }

    public String getComments() {
        return comments;
    }

    public LocalDateTime getInspectedAt() {
        return inspectedAt;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public Technician getInspector() {
        return inspector;
    }

    public void setStatus(InspectionStatus status) {
        this.status = status;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setInspectedAt(LocalDateTime inspectedAt) {
        this.inspectedAt = inspectedAt;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public void setInspector(Technician inspector) {
        this.inspector = inspector;
    }
}