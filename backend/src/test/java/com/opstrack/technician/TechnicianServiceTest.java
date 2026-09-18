package com.opstrack.technician;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TechnicianServiceTest {

    @Mock
    private TechnicianRepository technicianRepository;

    @InjectMocks
    private TechnicianService technicianService;

    @Test
    void shouldGetAllTechnicians() {
        Technician technician1 = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        Technician technician2 = new Technician(
                "Jordan",
                "Lee",
                "TECH-002",
                "Hydraulics",
                true
        );

        List<Technician> technicians =
                List.of(technician1, technician2);

        when(technicianRepository.findAll())
                .thenReturn(technicians);

        List<Technician> result =
                technicianService.getAllTechnicians();

        assertEquals(2, result.size());
        assertSame(technicians, result);

        verify(technicianRepository).findAll();
    }

    @Test
    void shouldGetTechnicianById() {
        Long technicianId = 1L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        Technician result =
                technicianService.getTechnicianById(
                        technicianId
                );

        assertSame(technician, result);

        verify(technicianRepository)
                .findById(technicianId);
    }

    @Test
    void shouldCreateTechnician() {
        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        when(technicianRepository.save(technician))
                .thenReturn(technician);

        Technician result =
                technicianService.createTechnician(
                        technician
                );

        assertSame(technician, result);

        verify(technicianRepository).save(technician);
    }

    @Test
    void shouldUpdateTechnician() {
        Long technicianId = 1L;

        Technician existingTechnician =
                new Technician(
                        "Alex",
                        "Carter",
                        "TECH-001",
                        "Avionics",
                        true
                );

        Technician updatedTechnician =
                new Technician(
                        "Alex",
                        "Carter",
                        "TECH-001",
                        "Hydraulics",
                        true
                );

        when(technicianRepository.findById(technicianId))
                .thenReturn(
                        Optional.of(existingTechnician)
                );

        when(technicianRepository.save(existingTechnician))
                .thenReturn(existingTechnician);

        Technician result =
                technicianService.updateTechnician(
                        technicianId,
                        updatedTechnician
                );

        assertEquals(
                "Hydraulics",
                result.getSpecialty()
        );

        assertEquals(
                "Alex",
                result.getFirstName()
        );

        assertEquals(
                "Carter",
                result.getLastName()
        );

        assertEquals(
                "TECH-001",
                result.getEmployeeNumber()
        );

        assertTrue(result.isActive());

        verify(technicianRepository)
                .findById(technicianId);

        verify(technicianRepository)
                .save(existingTechnician);
    }

    @Test
    void shouldUpdateTechnicianActiveStatus() {
        Long technicianId = 1L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        when(technicianRepository.save(technician))
                .thenReturn(technician);

        Technician result =
                technicianService
                        .updateTechnicianActiveStatus(
                                technicianId,
                                false
                        );

        assertFalse(result.isActive());

        verify(technicianRepository)
                .findById(technicianId);

        verify(technicianRepository)
                .save(technician);
    }

    @Test
    void shouldDeleteTechnician() {
        Long technicianId = 1L;

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.of(technician));

        technicianService.deleteTechnician(
                technicianId
        );

        verify(technicianRepository)
                .findById(technicianId);

        verify(technicianRepository)
                .delete(technician);
    }

    @Test
    void shouldThrowExceptionWhenTechnicianNotFound() {
        Long technicianId = 99L;

        when(technicianRepository.findById(technicianId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                technicianService
                                        .getTechnicianById(
                                                technicianId
                                        )
                );

        assertEquals(
                "Technician not found with id: 99",
                exception.getMessage()
        );

        verify(technicianRepository)
                .findById(technicianId);
    }
}