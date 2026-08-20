package com.example.Annuaire.Models;

public class CollusionThresholds {
    private int minMovementsForSuspicion = 1;
    private double claimsPerMonthThreshold = 3.0;
    private double percentDaysWithMultipleClaimsThreshold = 25.0;
    private double mostFrequentActePSPercentThreshold = 60.0;

    public CollusionThresholds() {

    }

    public CollusionThresholds(
            int minMovementsForSuspicion,
            double claimsPerMonthThreshold,
            double percentDaysWithMultipleClaimsThreshold,
            double mostFrequentActePSPercentThreshold) {
        this.minMovementsForSuspicion = minMovementsForSuspicion;
        this.claimsPerMonthThreshold = claimsPerMonthThreshold;
        this.percentDaysWithMultipleClaimsThreshold = percentDaysWithMultipleClaimsThreshold;
        this.mostFrequentActePSPercentThreshold = mostFrequentActePSPercentThreshold;
    }

    // Getters
    public int getMinMovementsForSuspicion() {
        return minMovementsForSuspicion;
    }

    public double getClaimsPerMonthThreshold() {
        return claimsPerMonthThreshold;
    }

    public double getPercentDaysWithMultipleClaimsThreshold() {
        return percentDaysWithMultipleClaimsThreshold;
    }

    public double getMostFrequentActePSPercentThreshold() {
        return mostFrequentActePSPercentThreshold;
    }
}
