package com.example.DTOS.ps;

import java.util.ArrayList;
import java.util.List;

public class HealthcareProfessionalStatsDTO {
    private List<SpecialtyDistributionDTO> specialtyDistribution;
    private List<RegionDistributionDTO> regionDistribution;
    private List<TopProfessionalDTO> topByVisits;
    private List<TopProfessionalDTO> topByTransactionVolume;
    private List<TopProfessionalDTO> topByAverageAmount;

    // Constructeurs, getters et setters
    public HealthcareProfessionalStatsDTO() {
        this.specialtyDistribution = new ArrayList<>();
        this.regionDistribution = new ArrayList<>();
        this.topByVisits = new ArrayList<>();
        this.topByTransactionVolume = new ArrayList<>();
        this.topByAverageAmount = new ArrayList<>();
    }

    // Getters et setters
    public List<SpecialtyDistributionDTO> getSpecialtyDistribution() {
        return specialtyDistribution;
    }

    public void setSpecialtyDistribution(List<SpecialtyDistributionDTO> specialtyDistribution) {
        this.specialtyDistribution = specialtyDistribution;
    }

    public List<RegionDistributionDTO> getRegionDistribution() {
        return regionDistribution;
    }

    public void setRegionDistribution(List<RegionDistributionDTO> regionDistribution) {
        this.regionDistribution = regionDistribution;
    }

    public List<TopProfessionalDTO> getTopByVisits() {
        return topByVisits;
    }

    public void setTopByVisits(List<TopProfessionalDTO> topByVisits) {
        this.topByVisits = topByVisits;
    }

    public List<TopProfessionalDTO> getTopByTransactionVolume() {
        return topByTransactionVolume;
    }

    public void setTopByTransactionVolume(List<TopProfessionalDTO> topByTransactionVolume) {
        this.topByTransactionVolume = topByTransactionVolume;
    }

    public List<TopProfessionalDTO> getTopByAverageAmount() {
        return topByAverageAmount;
    }

    public void setTopByAverageAmount(List<TopProfessionalDTO> topByAverageAmount) {
        this.topByAverageAmount = topByAverageAmount;
    }
}
