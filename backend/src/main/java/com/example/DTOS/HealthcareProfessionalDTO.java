package com.example.DTOS;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthcareProfessionalDTO {
    private Long id;
    private String name;
    private String medicalSpecialty;

    public HealthcareProfessionalDTO() {
    }

    public HealthcareProfessionalDTO(Long id, String name, String medicalSpecialty) {
        this.id = id;
        this.name = name;
        this.medicalSpecialty = medicalSpecialty;
    }
}
