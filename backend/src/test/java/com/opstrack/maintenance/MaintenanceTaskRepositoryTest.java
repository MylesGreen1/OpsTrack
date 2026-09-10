package com.opstrack.maintenance;

import com.opstrack.aircraft.Aircraft;
import com.opstrack.aircraft.AircraftRepository;
import com.opstrack.aircraft.AircraftStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MaintenanceTaskRepositoryTest {

    @Autowired
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Autowired
    private AircraftRepository aircraftRepository;



    @Test
    void shouldSaveMaintenanceTask() {
        Aircraft aircraft = new Aircraft();
        aircraft.setTailNumber("OT-001");
        aircraft.setAircraftType("F-16");
        aircraft.setStatus(AircraftStatus.MISSION_CAPABLE);
        aircraft.setLocation("Hangar 1");
        aircraft.setNotes("Test aircraft");

        aircraft = aircraftRepository.save(aircraft);

        MaintenanceTask task = new MaintenanceTask(
                "Inspect landing gear",
                "Perform scheduled landing gear inspection",
                MaintenanceStatus.OPEN,
                MaintenancePriority.HIGH,
                aircraft
        );

        MaintenanceTask savedTask = maintenanceTaskRepository.save(task);
        assertNotNull(savedTask.getId());

    }

    @Test
    void shouldFindMaintenanceTasksByAircraftId() {

        Aircraft aircraft = new Aircraft();
        aircraft.setTailNumber("OT-002");
        aircraft.setAircraftType("F-15E");
        aircraft.setStatus(AircraftStatus.MISSION_CAPABLE);
        aircraft.setLocation("Hangar 2");
        aircraft.setNotes("Aircraft for retrieval test");

        aircraft = aircraftRepository.save(aircraft);

        MaintenanceTask task1 = new MaintenanceTask(
                "Hydraulic inspection",
                "Inspect hydraulic system",
                MaintenanceStatus.OPEN,
                MaintenancePriority.HIGH,
                aircraft
        );

        MaintenanceTask task2 = new MaintenanceTask(
                "Tire inspection",
                "Inspect main landing gear tires",
                MaintenanceStatus.IN_PROGRESS,
                MaintenancePriority.MEDIUM,
                aircraft
        );

        maintenanceTaskRepository.save(task1);
        maintenanceTaskRepository.save(task2);

        List<MaintenanceTask> tasks =
                maintenanceTaskRepository.findByAircraftId(aircraft.getId());

        assertEquals(2, tasks.size());
        assertEquals(aircraft.getId(), tasks.get(0).getAircraft().getId());

    }

}
