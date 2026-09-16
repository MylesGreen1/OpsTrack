package com.opstrack.maintenance;

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
public class MaintenanceTaskServiceTest {

    @Mock
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private MaintenanceTaskService maintenanceTaskService;

    @Test
    void shouldCreateMaintenanceTask() {
        MaintenanceTask task = new MaintenanceTask();

        when(maintenanceTaskRepository.save(task))
                .thenReturn(task);

        MaintenanceTask result =
                maintenanceTaskService.createTask(task);

        assertSame(task, result);

        verify(maintenanceTaskRepository).save(task);
    }

    @Test
    void shouldGetAllMaintenanceTasks() {
        MaintenanceTask task1 = new MaintenanceTask();
        MaintenanceTask task2 = new MaintenanceTask();

        List<MaintenanceTask> tasks =
                List.of(task1, task2);

        when(maintenanceTaskRepository.findAll())
                .thenReturn(tasks);

        List<MaintenanceTask> result =
                maintenanceTaskService.getAllTasks();

        assertEquals(2, result.size());
        assertSame(tasks, result);

        verify(maintenanceTaskRepository).findAll();
    }

    @Test
    void shouldGetTasksByAircraftId() {
        Long aircraftId = 1L;

        MaintenanceTask task1 = new MaintenanceTask();
        MaintenanceTask task2 = new MaintenanceTask();

        List<MaintenanceTask> tasks =
                List.of(task1, task2);

        when(maintenanceTaskRepository.findByAircraftId(aircraftId))
                .thenReturn(tasks);

        List<MaintenanceTask> result =
                maintenanceTaskService.getTasksByAircraftId(aircraftId);

        assertEquals(2, result.size());

        verify(maintenanceTaskRepository)
                .findByAircraftId(aircraftId);
    }

    @Test
    void shouldUpdateMaintenanceTaskStatus() {
        Long taskId = 1L;

        MaintenanceTask task = new MaintenanceTask();
        task.setStatus(MaintenanceStatus.OPEN);

        when(maintenanceTaskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(maintenanceTaskRepository.save(task))
                .thenReturn(task);

        MaintenanceTask result =
                maintenanceTaskService.updateTaskStatus(
                        taskId,
                        MaintenanceStatus.IN_PROGRESS
                );

        assertEquals(
                MaintenanceStatus.IN_PROGRESS,
                result.getStatus()
        );

        verify(maintenanceTaskRepository).findById(taskId);
        verify(maintenanceTaskRepository).save(task);
    }

    @Test
    void shouldAssignTechnicianToMaintenanceTask() {
        Long taskId = 1L;
        Long technicianId = 2L;

        MaintenanceTask task = new MaintenanceTask();

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-002",
                "Avionics",
                true
        );

        when(maintenanceTaskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(maintenanceTaskRepository.save(task))
                .thenReturn(task);

        MaintenanceTask result =
                maintenanceTaskService.assignTechnician(
                        taskId,
                        technicianId
                );

        assertSame(
                technician,
                result.getTechnician()
        );

        verify(maintenanceTaskRepository).findById(taskId);
        verify(technicianRepository).findById(technicianId);
        verify(maintenanceTaskRepository).save(task);
    }
}