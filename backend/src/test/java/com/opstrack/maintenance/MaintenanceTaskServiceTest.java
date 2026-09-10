package com.opstrack.maintenance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MaintenanceTaskServiceTest {

    @Mock
    private MaintenanceTaskRepository maintenanceTaskRepository;

    @InjectMocks
    private MaintenanceTaskService maintenanceTaskService;

    @Test
    void shouldCreateMaintenanceTask() {
        MaintenanceTask task = new MaintenanceTask();

        when(maintenanceTaskRepository.save(task)).thenReturn(task);

        MaintenanceTask result = maintenanceTaskService.createTask(task);

        assertEquals(task, result);

        verify(maintenanceTaskRepository).save(task);
    }

    @Test
    void shouldGetTasksByAircraftId() {
        Long aircraftId = 1L;

        MaintenanceTask task1 = new MaintenanceTask();
        MaintenanceTask task2 = new MaintenanceTask();

        List<MaintenanceTask> tasks = List.of(task1, task2);

        when(maintenanceTaskRepository.findByAircraftId(aircraftId)).thenReturn(tasks);

        List<MaintenanceTask> result =
                maintenanceTaskService.getTasksByAircraftId(aircraftId);

        assertEquals(tasks, result);

        verify(maintenanceTaskRepository).findByAircraftId(aircraftId);

    }

}
