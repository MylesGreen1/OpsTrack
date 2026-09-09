package com.opstrack.aircraft;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tailNumber;

    private String aircraftType;

    @Enumerated(EnumType.STRING)
    private AircraftStatus status;

    private String location;

    private String notes;

    public Aircraft() {
    }

    public Aircraft(String tailNumber, String aircraftType,
                    AircraftStatus status, String location, String notes) {

        this.tailNumber = tailNumber;
        this.aircraftType = aircraftType;
        this.status = status;
        this.location = location;
        this.notes = notes;

    }

    public Long getId() {
        return id;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public AircraftStatus getStatus() {
        return status;
    }

    public void setStatus(AircraftStatus status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
