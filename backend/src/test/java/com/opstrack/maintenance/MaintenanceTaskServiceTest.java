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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import com.opstrack.aircraft.Aircraft;

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
    void shouldUpdateMaintenanceTask() {
        Long taskId = 1L;

        Aircraft originalAircraft = new Aircraft();
        Aircraft updatedAircraft = new Aircraft();

        MaintenanceTask existingTask = new MaintenanceTask();
        existingTask.setTitle("Old task");
        existingTask.setDescription("Old description");
        existingTask.setPriority(MaintenancePriority.LOW);
        existingTask.setStatus(MaintenanceStatus.OPEN);
        existingTask.setAircraft(originalAircraft);

        MaintenanceTask updatedTask = new MaintenanceTask();
        updatedTask.setTitle("Hydraulic system inspection");
        updatedTask.setDescription(
                "Inspect hydraulic lines and connections."
        );
        updatedTask.setPriority(MaintenancePriority.HIGH);
        updatedTask.setStatus(MaintenanceStatus.IN_PROGRESS);
        updatedTask.setAircraft(updatedAircraft);

        when(maintenanceTaskRepository.findById(taskId))
                .thenReturn(Optional.of(existingTask));

        when(maintenanceTaskRepository.save(existingTask))
                .thenReturn(existingTask);

        MaintenanceTask result =
                maintenanceTaskService.updateTask(
                        taskId,
                        updatedTask
                );

        assertEquals(
                "Hydraulic system inspection",
                result.getTitle()
        );

        assertEquals(
                "Inspect hydraulic lines and connections.",
                result.getDescription()
        );

        assertEquals(
                MaintenancePriority.HIGH,
                result.getPriority()
        );

        assertEquals(
                MaintenanceStatus.IN_PROGRESS,
                result.getStatus()
        );

        assertSame(
                updatedAircraft,
                result.getAircraft()
        );

        verify(maintenanceTaskRepository)
                .findById(taskId);

        verify(maintenanceTaskRepository)
                .save(existingTask);
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

    @Test
    void shouldDeleteMaintenanceTask() {

        Long taskId = 1L;

        MaintenanceTask task =
                new MaintenanceTask();

        when(maintenanceTaskRepository.findById(taskId))
                .thenReturn(Optional.of(task));

        maintenanceTaskService.deleteTask(taskId);

        verify(maintenanceTaskRepository)
                .findById(taskId);

        verify(maintenanceTaskRepository)
                .delete(task);
    }

    @Test
    void shouldThrowExceptionWhenDeletingMissingTask() {

        Long taskId = 99L;

        when(maintenanceTaskRepository.findById(taskId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> maintenanceTaskService
                                .deleteTask(taskId)
                );

        assertEquals(
                "Maintenance task not found with id: " + taskId,
                exception.getMessage()
        );

        verify(maintenanceTaskRepository)
                .findById(taskId);

        verify(maintenanceTaskRepository, never())
                .delete(
                        org.mockito.ArgumentMatchers
                                .any(MaintenanceTask.class)
                );
    }

}