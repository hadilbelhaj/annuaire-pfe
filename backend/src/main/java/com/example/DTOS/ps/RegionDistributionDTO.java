package com.example.DTOS.ps;

public class RegionDistributionDTO {
    private String regionName;
    private Long count;
    private Double percentage;

    // Constructeurs, getters et setters
    public RegionDistributionDTO(String regionName, Long count, Double percentage) {
        this.regionName = regionName;
        this.count = count;
        this.percentage = percentage;
    }

    // Getters et setters
    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
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
