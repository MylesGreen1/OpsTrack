package com.opstrack.technician;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TechnicianService {

    private final TechnicianRepository technicianRepository;

    public TechnicianService(
            TechnicianRepository technicianRepository
    ) {
        this.technicianRepository = technicianRepository;
    }

    public List<Technician> getAllTechnicians() {
        return technicianRepository.findAll();
    }

    public Technician getTechnicianById(Long id) {
        return technicianRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Technician not found with id: " + id
                ));
    }

    public Technician createTechnician(Technician technician) {
        return technicianRepository.save(technician);
    }

    public Technician updateTechnician(
            Long id,
            Technician updatedTechnician
    ) {
        Technician existingTechnician =
                getTechnicianById(id);

        existingTechnician.setFirstName(
                updatedTechnician.getFirstName()
        );

        existingTechnician.setLastName(
                updatedTechnician.getLastName()
        );

        existingTechnician.setEmployeeNumber(
                updatedTechnician.getEmployeeNumber()
        );

        existingTechnician.setSpecialty(
                updatedTechnician.getSpecialty()
        );

        existingTechnician.setActive(
                updatedTechnician.isActive()
        );

        return technicianRepository.save(
                existingTechnician
        );
    }

    public Technician updateTechnicianActiveStatus(
            Long id,
            boolean active
    ) {
        Technician technician =
                getTechnicianById(id);

        technician.setActive(active);

        return technicianRepository.save(technician);
    }

    public void deleteTechnician(Long id) {
        Technician technician =
                getTechnicianById(id);

        technicianRepository.delete(technician);
    }
}