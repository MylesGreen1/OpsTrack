package com.opstrack.worknote;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.maintenance.MaintenanceTaskRepository;
import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WorkNoteServiceTest {

    @Mock
    private WorkNoteRepository workNoteRepository;

    @Mock
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private WorkNoteService workNoteService;

    @Test
    void shouldCreateWorkNote() {
        Long maintenanceTaskId = 1L;
        Long technicianId = 2L;
        String noteText = "Replaced damaged hydraulic fitting.";

        MaintenanceTask maintenanceTask = new MaintenanceTask();

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-005",
                "Hydraulics",
                true
        );

        when(maintenanceTaskRepository.findById(maintenanceTaskId))
                .thenReturn(Optional.of(maintenanceTask));

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(workNoteRepository.save(
                org.mockito.ArgumentMatchers.any(WorkNote.class)
        )).thenAnswer(invocation -> invocation.getArgument(0));

        WorkNote result = workNoteService.createWorkNote(
                maintenanceTaskId,
                technicianId,
                noteText
        );

        assertEquals(noteText, result.getNote());
        assertSame(maintenanceTask, result.getMaintenanceTask());
        assertSame(technician, result.getTechnician());

        verify(maintenanceTaskRepository).findById(maintenanceTaskId);
        verify(technicianRepository).findById(technicianId);
        verify(workNoteRepository).save(
                org.mockito.ArgumentMatchers.any(WorkNote.class)
        );
    }

    @Test
    void shouldGetWorkNotesByMaintenanceTaskId() {
        Long maintenanceTaskId = 1L;

        WorkNote note1 = new WorkNote();
        WorkNote note2 = new WorkNote();

        List<WorkNote> workNotes = List.of(note1, note2);

        when(workNoteRepository.findByMaintenanceTaskId(maintenanceTaskId))
                .thenReturn(workNotes);

        List<WorkNote> result =
                workNoteService.getWorkNotesByMaintenanceTaskId(
                        maintenanceTaskId
                );

        assertEquals(2, result.size());

        verify(workNoteRepository)
                .findByMaintenanceTaskId(maintenanceTaskId);
    }
}