package com.opstrack.aircraft;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public List<Aircraft> getAllAircraft() {
        return aircraftRepository.findAll();
    }

    public Aircraft getAircraftById(Long id) {
        return aircraftRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Aircraft not found with id: " + id
                        )
                );
    }

    public Aircraft createAircraft(Aircraft aircraft) {

        if (aircraftRepository
                .findByTailNumber(aircraft.getTailNumber())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Aircraft already exists with tail number: "
                            + aircraft.getTailNumber()
            );
        }

        return aircraftRepository.save(aircraft);
    }

    public Aircraft updateAircraft(
            Long id,
            Aircraft updatedAircraft
    ) {

        Aircraft aircraft = getAircraftById(id);

        aircraft.setTailNumber(
                updatedAircraft.getTailNumber()
        );

        aircraft.setAircraftType(
                updatedAircraft.getAircraftType()
        );

        aircraft.setStatus(
                updatedAircraft.getStatus()
        );

        aircraft.setLocation(
                updatedAircraft.getLocation()
        );

        aircraft.setNotes(
                updatedAircraft.getNotes()
        );

        return aircraftRepository.save(aircraft);
    }

    public Aircraft updateAircraftStatus(
            Long id,
            AircraftStatus status
    ) {

        Aircraft aircraft = getAircraftById(id);

        aircraft.setStatus(status);

        return aircraftRepository.save(aircraft);
    }

    public void deleteAircraft(Long id) {

        Aircraft aircraft = getAircraftById(id);

        aircraftRepository.delete(aircraft);
    }
}