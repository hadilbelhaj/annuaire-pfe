package com.example.Annuaire.Models;

public class MedicalClaim {
    private double amount;
    private double specialtyAverageAmount;
    private String medicalSpecialty;
    private String designation;
    private double reimbursementPercentage;

    // Getters and setters
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSpecialtyAverageAmount() {
        return specialtyAverageAmount;
    }

    public void setSpecialtyAverageAmount(double specialtyAverageAmount) {
        this.specialtyAverageAmount = specialtyAverageAmount;
    }

    public String getMedicalSpecialty() {
        return medicalSpecialty;
    }

    public void setMedicalSpecialty(String medicalSpecialty) {
        this.medicalSpecialty = medicalSpecialty;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public double getReimbursementPercentage() {
        return reimbursementPercentage;
    }

    public void setReimbursementPercentage(double reimbursementPercentage) {
        this.reimbursementPercentage = reimbursementPercentage;
    }
}
