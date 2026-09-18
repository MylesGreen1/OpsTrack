package com.opstrack.worknote;

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
public class WorkNoteRepositoryTest {

    @Autowired
    private WorkNoteRepository workNoteRepository;

    @Autowired
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private TechnicianRepository technicianRepository;

    @Test
    void shouldSaveWorkNote() {
        Aircraft aircraft = new Aircraft(
                "OT-004",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 4",
                "Aircraft for work note test"
        );

        aircraftRepository.save(aircraft);

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-003",
                "Avionics",
                true
        );

        technicianRepository.save(technician);

        MaintenanceTask task = new MaintenanceTask(
                "Avionics inspection",
                "Inspect aircraft avionics systems",
                MaintenanceStatus.OPEN,
                MaintenancePriority.HIGH,
                aircraft
        );

        task.setTechnician(technician);

        maintenanceTaskRepository.save(task);

        WorkNote workNote = new WorkNote(
                "Inspected avionics wiring and found no damage.",
                task,
                technician
        );

        WorkNote savedWorkNote = workNoteRepository.save(workNote);

        assertNotNull(savedWorkNote.getId());
        assertNotNull(savedWorkNote.getCreatedAt());
        assertEquals(
                "Inspected avionics wiring and found no damage.",
                savedWorkNote.getNote()
        );
    }

    @Test
    void shouldFindWorkNotesByMaintenanceTaskId() {
        Aircraft aircraft = new Aircraft(
                "OT-005",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 5",
                "Aircraft for work note lookup test"
        );

        aircraftRepository.save(aircraft);

        Technician technician = new Technician(
                "Jordan",
                "Miles",
                "TECH-004",
                "Hydraulics",
                true
        );

        technicianRepository.save(technician);

        MaintenanceTask task = new MaintenanceTask(
                "Hydraulic inspection",
                "Inspect hydraulic system",
                MaintenanceStatus.IN_PROGRESS,
                MaintenancePriority.HIGH,
                aircraft
        );

        task.setTechnician(technician);

        maintenanceTaskRepository.save(task);

        WorkNote note1 = new WorkNote(
                "Inspected hydraulic lines.",
                task,
                technician
        );

        WorkNote note2 = new WorkNote(
                "Found minor leak near pump fitting.",
                task,
                technician
        );

        workNoteRepository.save(note1);
        workNoteRepository.save(note2);

        List<WorkNote> workNotes =
                workNoteRepository.findByMaintenanceTaskId(task.getId());

        assertEquals(2, workNotes.size());
    }
}