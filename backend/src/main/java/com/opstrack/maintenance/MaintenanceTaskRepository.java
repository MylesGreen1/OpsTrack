package com.opstrack.maintenance;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceTaskRepository extends JpaRepository<MaintenanceTask, Long> {

    List<MaintenanceTask> findByAircraftId(Long aircraftId);



}