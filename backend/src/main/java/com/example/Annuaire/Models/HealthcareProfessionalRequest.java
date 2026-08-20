package com.example.Annuaire.Models;

import java.util.List;

public class HealthcareProfessionalRequest {
    private HealthcareProfessional healthcareProfessional;
    private List<String> prestationLabels;

    public HealthcareProfessional getHealthcareProfessional() {
        return healthcareProfessional;
    }

    public void setHealthcareProfessional(HealthcareProfessional healthcareProfessional) {
        this.healthcareProfessional = healthcareProfessional;
    }

    public List<String> getPrestationLabels() {
        return prestationLabels;
    }

    public void setPrestationLabels(List<String> prestationLabels) {
        this.prestationLabels = prestationLabels;
    }
}
