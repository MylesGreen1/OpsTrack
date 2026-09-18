package com.opstrack.parts;

import com.opstrack.maintenance.MaintenanceTask;
import com.opstrack.maintenance.MaintenanceTaskRepository;
import com.opstrack.technician.Technician;
import com.opstrack.technician.TechnicianRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartRequestServiceTest {
    @Mock PartRequestRepository repository;
    @Mock MaintenanceTaskRepository taskRepository;
    @Mock TechnicianRepository technicianRepository;
    private PartRequestService service;

    @BeforeEach void setUp() { service = new PartRequestService(repository, taskRepository, technicianRepository); }

    @Test void shouldCreatePartRequestForTaskAndTechnician() {
        MaintenanceTask task = new MaintenanceTask();
        Technician technician = new Technician();
        PartRequest request = new PartRequest();
        request.setPartNumber("PN-1"); request.setPartName("Filter"); request.setQuantity(2);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(technicianRepository.findById(2L)).thenReturn(Optional.of(technician));
        when(repository.save(request)).thenReturn(request);

        PartRequest result = service.create(1L, 2L, request);

        assertSame(task, result.getMaintenanceTask());
        assertSame(technician, result.getRequestedBy());
        assertEquals(PartRequestStatus.REQUESTED, result.getStatus());
        verify(repository).save(request);
    }

    @Test void shouldUpdatePartRequestStatus() {
        PartRequest request = new PartRequest();
        when(repository.findById(3L)).thenReturn(Optional.of(request));
        when(repository.save(request)).thenReturn(request);
        assertEquals(PartRequestStatus.ORDERED, service.updateStatus(3L, PartRequestStatus.ORDERED).getStatus());
    }

    @Test void shouldRejectMissingPartRequest() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(99L, PartRequestStatus.RECEIVED));
    }
}
