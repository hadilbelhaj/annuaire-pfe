package com.example.DTOS.MovementsTable;

public class GroupingByPsDto {
    private String healthcareProfessionalName;
    private String medicalSpecialty;
    private String ref;
    private Long totalVisits;
    private Double totalAmount;

    public GroupingByPsDto(String healthcareProfessionalName, String medicalSpecialty, String ref, Long totalVisits,
            Double totalAmount) {
        this.healthcareProfessionalName = healthcareProfessionalName;
        this.medicalSpecialty = medicalSpecialty;
        this.ref = ref;
        this.totalVisits = totalVisits;
        this.totalAmount = totalAmount;
    }

    public String getHealthcareProfessionalName() {
        return healthcareProfessionalName;
    }

    public void setHealthcareProfessionalName(String healthcareProfessionalName) {
        this.healthcareProfessionalName = healthcareProfessionalName;
    }

    public String getMedicalSpecialty() {
        return medicalSpecialty;
    }

    public void setMedicalSpecialty(String medicalSpecialty) {
        this.medicalSpecialty = medicalSpecialty;
    }

    public Long getTotalVisits() {
        return totalVisits;
    }

    public void setTotalVisits(Long totalVisits) {
        this.totalVisits = totalVisits;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        return "GroupingByPsDto{" +
                "healthcareProfessionalName='" + healthcareProfessionalName + '\'' +
                ", medicalSpecialty='" + medicalSpecialty + '\'' +
                ", ref='" + ref + '\'' +
                ", totalVisits=" + totalVisits +
                ", totalAmount=" + totalAmount +
                '}';
    }
}
