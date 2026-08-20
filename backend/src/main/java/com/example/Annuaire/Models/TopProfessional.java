package com.example.Annuaire.Models;

import java.math.BigDecimal;

public class TopProfessional {
    private Long id;
    private String name;
    private String specialty;
    private String region;
    private Integer patientCount;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;

    public TopProfessional(Long id, String name, String specialty, String region,
            Integer patientCount, BigDecimal totalAmount, BigDecimal averageAmount) {
        this.id = id;
        this.name = name;
        this.specialty = specialty;
        this.region = region;
        this.patientCount = patientCount;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public String getRegion() {
        return region;
    }

    public Integer getPatientCount() {
        return patientCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }
}
