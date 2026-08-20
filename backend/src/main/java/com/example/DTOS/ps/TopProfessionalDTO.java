package com.example.DTOS.ps;

import java.math.BigDecimal;

public class TopProfessionalDTO {
    private Long id;
    private String name;
    private String medicalSpecialty;
    private String region;
    private Long visitCount;
    private Long transactionCount;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    public TopProfessionalDTO(){}

    public TopProfessionalDTO(Long id, String name, String medicalSpecialty, String region,
            Long visitCount, Long transactionCount,
            Double totalAmount, Double averageAmount) {
        this.id = id;
        this.name = name;
        this.medicalSpecialty = medicalSpecialty;
        this.region = region;
        this.visitCount = visitCount;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount != null ? new BigDecimal(totalAmount) : null;
        this.averageAmount = averageAmount != null ? new BigDecimal(averageAmount) : null;
    }

    public TopProfessionalDTO(Long id, String name, String medicalSpecialty, String region) {
        this.id = id;
        this.name = name;
        this.medicalSpecialty = medicalSpecialty;
        this.averageAmount = averageAmount;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMedicalSpecialty() {
        return medicalSpecialty;
    }

    public void setMedicalSpecialty(String medicalSpecialty) {
        this.medicalSpecialty = medicalSpecialty;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Long getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(Long visitCount) {
        this.visitCount = visitCount;
    }

    public Long getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(Long transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }
}
