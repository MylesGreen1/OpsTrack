package com.opstrack.maintenance;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import com.opstrack.aircraft.Aircraft;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class MaintenanceTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private MaintenanceStatus status;

    @Enumerated(EnumType.STRING)
    private MaintenancePriority priority;

    @ManyToOne
    @JoinColumn(name = "aircraft_id")
    private Aircraft aircraft;

    public MaintenanceTask() {
    }

    public MaintenanceTask(String title, String description,
                           MaintenanceStatus status,
                           MaintenancePriority priority,
                           Aircraft aircraft) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.aircraft = aircraft;

    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public MaintenanceStatus getStatus() {
        return status;
    }

    public MaintenancePriority getPriority() {
        return priority;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(MaintenanceStatus status) {
        this.status = status;
    }

    public void setPriority(MaintenancePriority priority) {
        this.priority = priority;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }
}
