package com.opstrack.aircraft;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AircraftServiceTest {

    @Mock
    private AircraftRepository aircraftRepository;

    private AircraftService aircraftService;

    @BeforeEach
    void setUp() {
        aircraftService = new AircraftService(
                aircraftRepository
        );
    }

    @Test
    void shouldReturnAllAircraft() {

        Aircraft aircraftOne = new Aircraft(
                "AF-001",
                "F-16",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 1",
                "Ready for operations"
        );

        Aircraft aircraftTwo = new Aircraft(
                "AF-002",
                "C-130",
                AircraftStatus.IN_MAINTENANCE,
                "Hangar 2",
                "Scheduled maintenance"
        );

        when(aircraftRepository.findAll())
                .thenReturn(
                        List.of(
                                aircraftOne,
                                aircraftTwo
                        )
                );

        List<Aircraft> aircraft =
                aircraftService.getAllAircraft();

        assertEquals(2, aircraft.size());

        assertEquals(
                "AF-001",
                aircraft.get(0).getTailNumber()
        );

        assertEquals(
                "AF-002",
                aircraft.get(1).getTailNumber()
        );

        verify(aircraftRepository).findAll();
    }

    @Test
    void shouldReturnAircraftById() {

        Aircraft aircraft = new Aircraft(
                "AF-003",
                "F-22",
                AircraftStatus.MISSION_CAPABLE,
                "Flight Line",
                "Operational"
        );

        when(aircraftRepository.findById(1L))
                .thenReturn(Optional.of(aircraft));

        Aircraft result =
                aircraftService.getAircraftById(1L);

        assertEquals(
                "AF-003",
                result.getTailNumber()
        );

        assertEquals(
                AircraftStatus.MISSION_CAPABLE,
                result.getStatus()
        );

        verify(aircraftRepository)
                .findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAircraftNotFound() {

        when(aircraftRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                aircraftService
                                        .getAircraftById(99L)
                );

        assertEquals(
                "Aircraft not found with id: 99",
                exception.getMessage()
        );

        verify(aircraftRepository)
                .findById(99L);
    }

    @Test
    void shouldCreateAircraft() {

        Aircraft aircraft = new Aircraft(
                "AF-004",
                "F-35",
                AircraftStatus.MISSION_CAPABLE,
                "Hangar 3",
                "New aircraft record"
        );

        when(
                aircraftRepository.findByTailNumber(
                        "AF-004"
                )
        ).thenReturn(Optional.empty());

        when(aircraftRepository.save(aircraft))
                .thenReturn(aircraft);

        Aircraft savedAircraft =
                aircraftService.createAircraft(
                        aircraft
                );

        assertEquals(
                "AF-004",
                savedAircraft.getTailNumber()
        );

        assertEquals(
                AircraftStatus.MISSION_CAPABLE,
                savedAircraft.getStatus()
        );

        verify(aircraftRepository)
                .findByTailNumber("AF-004");

        verify(aircraftRepository)
                .save(aircraft);
    }

    @Test
    void shouldRejectDuplicateTailNumber() {

        Aircraft aircraft = new Aircraft(
                "AF-005",
                "C-17",
                AircraftStatus.MISSION_CAPABLE,
                "Ramp",
                "Existing aircraft"
        );

        when(
                aircraftRepository.findByTailNumber(
                        "AF-005"
                )
        ).thenReturn(Optional.of(aircraft));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () ->
                                aircraftService
                                        .createAircraft(
                                                aircraft
                                        )
                );

        assertEquals(
                "Aircraft already exists with tail number: AF-005",
                exception.getMessage()
        );

        verify(aircraftRepository)
                .findByTailNumber("AF-005");

        verify(
                aircraftRepository,
                never()
        ).save(any(Aircraft.class));
    }

    @Test
    void shouldUpdateAircraftStatus() {

        Aircraft aircraft = new Aircraft(
                "AF-006",
                "F-15",
                AircraftStatus.MISSION_CAPABLE,
                "Flight Line",
                "Operational"
        );

        when(aircraftRepository.findById(1L))
                .thenReturn(Optional.of(aircraft));

        when(aircraftRepository.save(aircraft))
                .thenReturn(aircraft);

        Aircraft updatedAircraft =
                aircraftService.updateAircraftStatus(
                        1L,
                        AircraftStatus.IN_MAINTENANCE
                );

        assertEquals(
                AircraftStatus.IN_MAINTENANCE,
                updatedAircraft.getStatus()
        );

        verify(aircraftRepository)
                .findById(1L);

        verify(aircraftRepository)
                .save(aircraft);
    }

    @Test
    void shouldDeleteAircraft() {

        Aircraft aircraft = new Aircraft(
                "AF-007",
                "KC-135",
                AircraftStatus.NON_MISSION_CAPABLE,
                "Hangar 4",
                "Awaiting repair"
        );

        when(aircraftRepository.findById(1L))
                .thenReturn(Optional.of(aircraft));

        aircraftService.deleteAircraft(1L);

        verify(aircraftRepository)
                .findById(1L);

        verify(aircraftRepository)
                .delete(aircraft);
    }
}