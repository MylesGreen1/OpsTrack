package com.opstrack.parts;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {
    List<PartRequest> findByMaintenanceTaskId(Long maintenanceTaskId);
    List<PartRequest> findByRequestedById(Long technicianId);
}
