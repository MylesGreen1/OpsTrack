package com.opstrack.aircraft;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import static org.junit.jupiter.api.Assertions.assertEquals;



@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AircraftRepositoryTest {

    @Autowired
    private AircraftRepository aircraftRepository;

    @Test
    void shouldSaveAircraft() {

        Aircraft aircraft = new Aircraft(
                "AF-001",
                "F-15E",
                AircraftStatus.MISSION_CAPABLE,
                "Flightline",
                "Ready for operations"
        );

        Aircraft savedAircraft = aircraftRepository.save(aircraft);

        assertNotNull(savedAircraft.getId());

    }

    @Test
    void shouldFindAircraftByTailNumber() {
        Aircraft aircraft = new Aircraft(
                "AF-002",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 2",
                "Awaiting inspection"
        );

        aircraftRepository.save(aircraft);

        Aircraft foundAircraft = aircraftRepository
                .findByTailNumber("AF-002")
                .orElseThrow();

        assertNotNull(foundAircraft.getId());
        assertEquals("AF-002", foundAircraft.getTailNumber());
    }

}
