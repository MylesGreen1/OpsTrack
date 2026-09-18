package com.opstrack.inspection;

import com.opstrack.maintenance.MaintenanceStatus;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class InspectionServiceTest {

    @Mock
    private InspectionRepository inspectionRepository;

    @Mock
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private InspectionService inspectionService;

    @Test
    void shouldApproveInspectionAndCompleteMaintenanceTask() {
        Long maintenanceTaskId = 1L;
        Long inspectorId = 2L;

        MaintenanceTask task = new MaintenanceTask();
        task.setStatus(MaintenanceStatus.IN_PROGRESS);

        Technician inspector = new Technician(
                "Morgan",
                "Reed",
                "QA-003",
                "Quality Assurance",
                true
        );

        when(maintenanceTaskRepository.findById(maintenanceTaskId))
                .thenReturn(Optional.of(task));

        when(technicianRepository.findById(inspectorId))
                .thenReturn(Optional.of(inspector));

        when(maintenanceTaskRepository.save(task))
                .thenReturn(task);

        when(inspectionRepository.save(any(Inspection.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inspection result = inspectionService.createInspection(
                maintenanceTaskId,
                inspectorId,
                InspectionStatus.APPROVED,
                "Inspection passed."
        );

        assertEquals(
                InspectionStatus.APPROVED,
                result.getStatus()
        );

        assertEquals(
                MaintenanceStatus.COMPLETED,
                task.getStatus()
        );

        assertSame(
                inspector,
                result.getInspector()
        );

        verify(maintenanceTaskRepository)
                .findById(maintenanceTaskId);

        verify(technicianRepository)
                .findById(inspectorId);

        verify(maintenanceTaskRepository)
                .save(task);

        verify(inspectionRepository)
                .save(any(Inspection.class));
    }

    @Test
    void shouldRejectInspectionAndReturnTaskToInProgress() {
        Long maintenanceTaskId = 1L;
        Long inspectorId = 2L;

        MaintenanceTask task = new MaintenanceTask();
        task.setStatus(MaintenanceStatus.COMPLETED);

        Technician inspector = new Technician(
                "Taylor",
                "Brooks",
                "QA-004",
                "Quality Assurance",
                true
        );

        when(maintenanceTaskRepository.findById(maintenanceTaskId))
                .thenReturn(Optional.of(task));

        when(technicianRepository.findById(inspectorId))
                .thenReturn(Optional.of(inspector));

        when(maintenanceTaskRepository.save(task))
                .thenReturn(task);

        when(inspectionRepository.save(any(Inspection.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Inspection result = inspectionService.createInspection(
                maintenanceTaskId,
                inspectorId,
                InspectionStatus.REJECTED,
                "Loose connector found."
        );

        assertEquals(
                InspectionStatus.REJECTED,
                result.getStatus()
        );

        assertEquals(
                MaintenanceStatus.IN_PROGRESS,
                task.getStatus()
        );

        verify(maintenanceTaskRepository)
                .save(task);

        verify(inspectionRepository)
                .save(any(Inspection.class));
    }

    @Test
    void shouldGetAllInspections() {
        Inspection inspection1 = new Inspection();
        Inspection inspection2 = new Inspection();

        List<Inspection> inspections =
                List.of(inspection1, inspection2);

        when(inspectionRepository.findAll())
                .thenReturn(inspections);

        List<Inspection> result =
                inspectionService.getAllInspections();

        assertEquals(2, result.size());
        assertSame(inspections, result);

        verify(inspectionRepository).findAll();
    }

    @Test
    void shouldGetInspectionsByMaintenanceTaskId() {
        Long maintenanceTaskId = 1L;

        Inspection inspection1 = new Inspection();
        Inspection inspection2 = new Inspection();

        List<Inspection> inspections =
                List.of(inspection1, inspection2);

        when(
                inspectionRepository.findByMaintenanceTaskId(
                        maintenanceTaskId
                )
        ).thenReturn(inspections);

        List<Inspection> result =
                inspectionService
                        .getInspectionsByMaintenanceTaskId(
                                maintenanceTaskId
                        );

        assertEquals(2, result.size());

        verify(inspectionRepository)
                .findByMaintenanceTaskId(
                        maintenanceTaskId
                );
    }
}