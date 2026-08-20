package com.example.Annuaire.Models;

public class SpecialtyVolume {
    private String specialty;
    private Long count;

    public SpecialtyVolume(String specialty, Long count) {
        this.specialty = specialty;
        this.count = count;
    }

    public String getSpecialty() {
        return specialty;
    }

    public Long getCount() {
        return count;
    }
}
