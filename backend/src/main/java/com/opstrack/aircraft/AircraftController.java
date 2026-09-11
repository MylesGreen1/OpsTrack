package com.opstrack.aircraft;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aircraft")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public List<Aircraft> getAllAircraft() {
        return aircraftService.getAllAircraft();
    }

    @GetMapping("/{id}")
    public Aircraft getAircraftById(
            @PathVariable Long id
    ) {
        return aircraftService.getAircraftById(id);
    }

    @PostMapping
    public Aircraft createAircraft(
            @RequestBody Aircraft aircraft
    ) {
        return aircraftService.createAircraft(aircraft);
    }

    @PutMapping("/{id}")
    public Aircraft updateAircraft(
            @PathVariable Long id,
            @RequestBody Aircraft aircraft
    ) {
        return aircraftService.updateAircraft(
                id,
                aircraft
        );
    }

    @PatchMapping("/{id}/status")
    public Aircraft updateAircraftStatus(
            @PathVariable Long id,
            @RequestParam AircraftStatus status
    ) {
        return aircraftService.updateAircraftStatus(
                id,
                status
        );
    }

    @DeleteMapping("/{id}")
    public void deleteAircraft(
            @PathVariable Long id
    ) {
        aircraftService.deleteAircraft(id);
    }
}