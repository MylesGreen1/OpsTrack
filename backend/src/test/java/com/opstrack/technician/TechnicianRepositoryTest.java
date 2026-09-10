package com.opstrack.technician;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TechnicianRepositoryTest {

    @Autowired
    private TechnicianRepository technicianRepository;

    @Test
    void shouldSaveTechnician() {

        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        Technician savedTechnician = technicianRepository.save(technician);

        assertNotNull(savedTechnician.getId());

    }

    @Test
    void shouldFindTechnicianByEmployeeNumber() {
        Technician technician = new Technician(
                "Alex",
                "Carter",
                "TECH-001",
                "Avionics",
                true
        );

        technicianRepository.save(technician);

        Technician foundTechnician = technicianRepository
                .findByEmployeeNumber("TECH-001")
                .orElseThrow();

        assertEquals("TECH-001", foundTechnician.getEmployeeNumber());
    }
}