package com.opstrack.parts;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.technician.Technician;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "part_requests")
public class PartRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String partNumber;

    @NotBlank
    @Column(nullable = false)
    private String partName;

    @Min(1)
    @Column(nullable = false)
    private int quantity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PartRequestStatus status = PartRequestStatus.REQUESTED;

    @ManyToOne(optional = false)
    @JoinColumn(name = "maintenance_task_id", nullable = false)
    private MaintenanceTask maintenanceTask;

    @ManyToOne(optional = false)
    @JoinColumn(name = "requested_by_technician_id", nullable = false)
    private Technician requestedBy;

    @Column(nullable = false, updatable = false)
    private LocalDateTime requestedAt;

    private String notes;

    public PartRequest() {}

    @PrePersist
    void onCreate() {
        if (requestedAt == null) requestedAt = LocalDateTime.now();
        if (status == null) status = PartRequestStatus.REQUESTED;
    }

    public Long getId() { return id; }
    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }
    public String getPartName() { return partName; }
    public void setPartName(String partName) { this.partName = partName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public PartRequestStatus getStatus() { return status; }
    public void setStatus(PartRequestStatus status) { this.status = status; }
    public MaintenanceTask getMaintenanceTask() { return maintenanceTask; }
    public void setMaintenanceTask(MaintenanceTask maintenanceTask) { this.maintenanceTask = maintenanceTask; }
    public Technician getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Technician requestedBy) { this.requestedBy = requestedBy; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
