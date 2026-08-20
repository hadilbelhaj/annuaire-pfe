package com.example.DTOS.ps;

public class SpecialtyDistributionDTO {
    private String specialtyName;
    private Long count;
    private Double percentage;

    // Constructeurs, getters et setters
    public SpecialtyDistributionDTO(String specialtyName, Long count, Double percentage) {
        this.specialtyName = specialtyName;
        this.count = count;
        this.percentage = percentage;
    }

    // Getters et setters
    public String getSpecialtyName() {
        return specialtyName;
    }

    public void setSpecialtyName(String specialtyName) {
        this.specialtyName = specialtyName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }
}
