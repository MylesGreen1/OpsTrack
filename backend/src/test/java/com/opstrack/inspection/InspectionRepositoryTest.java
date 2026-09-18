package com.opstrack.inspection;

import com.opstrack.aircraft.Aircraft;
import com.opstrack.aircraft.AircraftRepository;
import com.opstrack.aircraft.AircraftStatus;
import com.opstrack.maintenance.MaintenancePriority;
import com.opstrack.maintenance.MaintenanceStatus;
import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.maintenance.MaintenanceTaskRepository;
import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class InspectionRepositoryTest {

    @Autowired
    private InspectionRepository inspectionRepository;

    @Autowired
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Test
    void shouldSaveInspection() {
        Aircraft aircraft = new Aircraft(
                "OT-006",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 6",
                "Aircraft for inspection test"
        );

        aircraftRepository.save(aircraft);

        Technician inspector = new Technician(
                "Morgan",
                "Reed",
                "QA-001",
                "Quality Assurance",
                true
        );

        technicianRepository.save(inspector);

        MaintenanceTask task = new MaintenanceTask(
                "Flight control inspection",
                "Inspect flight control system",
                MaintenanceStatus.IN_PROGRESS,
                MaintenancePriority.HIGH,
                aircraft
        );

        maintenanceTaskRepository.save(task);

        Inspection inspection = new Inspection(
                InspectionStatus.APPROVED,
                "Inspection completed with no discrepancies.",
                task,
                inspector
        );

        Inspection savedInspection =
                inspectionRepository.save(inspection);

        assertNotNull(savedInspection.getId());
        assertNotNull(savedInspection.getInspectedAt());
        assertEquals(
                InspectionStatus.APPROVED,
                savedInspection.getStatus()
        );
        assertEquals(
                "QA-001",
                savedInspection.getInspector().getEmployeeNumber()
        );
    }

    @Test
    void shouldFindInspectionsByMaintenanceTaskId() {
        Aircraft aircraft = new Aircraft(
                "OT-007",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 7",
                "Aircraft for inspection history test"
        );

        aircraftRepository.save(aircraft);

        Technician inspector = new Technician(
                "Taylor",
                "Brooks",
                "QA-002",
                "Quality Assurance",
                true
        );

        technicianRepository.save(inspector);

        MaintenanceTask task = new MaintenanceTask(
                "Engine inspection",
                "Inspect engine components",
                MaintenanceStatus.IN_PROGRESS,
                MaintenancePriority.HIGH,
                aircraft
        );

        maintenanceTaskRepository.save(task);

        Inspection firstInspection = new Inspection(
                InspectionStatus.REJECTED,
                "Loose connector found during inspection.",
                task,
                inspector
        );

        Inspection secondInspection = new Inspection(
                InspectionStatus.APPROVED,
                "Connector corrected and reinspection passed.",
                task,
                inspector
        );

        inspectionRepository.save(firstInspection);
        inspectionRepository.save(secondInspection);

        List<Inspection> inspections =
                inspectionRepository.findByMaintenanceTaskId(task.getId());

        assertEquals(2, inspections.size());
    }
}