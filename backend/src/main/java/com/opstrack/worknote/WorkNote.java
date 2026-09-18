package com.opstrack.worknote;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.technician.Technician;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "work_notes")
public class WorkNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String note;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "maintenance_task_id")
    private MaintenanceTask maintenanceTask;

    @ManyToOne
    @JoinColumn(name = "technician_id")
    private Technician technician;

    public WorkNote() {
    }

    public WorkNote(
            String note,
            MaintenanceTask maintenanceTask,
            Technician technician
    ) {
        this.note = note;
        this.maintenanceTask = maintenanceTask;
        this.technician = technician;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNote() {
        return note;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public MaintenanceTask getMaintenanceTask() {
        return maintenanceTask;
    }

    public Technician getTechnician() {
        return technician;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setMaintenanceTask(MaintenanceTask maintenanceTask) {
        this.maintenanceTask = maintenanceTask;
    }

    public void setTechnician(Technician technician) {
        this.technician = technician;
    }
}