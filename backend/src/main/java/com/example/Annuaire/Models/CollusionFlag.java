package com.example.Annuaire.Models;

public class CollusionFlag {
    private final DoctorAdherentPair pair;
    private final String doctorName;
    private final String adherentName;
    private final CollusionIndicators indicators;
    private final int riskScore;

    public CollusionFlag(
            DoctorAdherentPair pair,
            String doctorName,
            String adherentName,
            CollusionIndicators indicators,
            int riskScore) {
        this.pair = pair;
        this.doctorName = doctorName;
        this.adherentName = adherentName;
        this.indicators = indicators;
        this.riskScore = riskScore;
    }

    public DoctorAdherentPair getPair() {
        return pair;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public String getAdherentName() {
        return adherentName;
    }

    public CollusionIndicators getIndicators() {
        return indicators;
    }

    public int getRiskScore() {
        return riskScore;
    }

    @Override
    public String toString() {
        return String.format(
                "Possible collusion detected between Dr. %s and adherent %s (Risk Score: %d/100)\n" +
                        "• Claims per month: %.2f\n" +
                        "• Days with multiple claims: %d (%.1f%%)\n" +
                        "• Most frequent procedure: %s (%d times, %.1f%% of claims)\n" +
                        "• Average claim amount: %s",
                doctorName, adherentName, riskScore,
                indicators.getClaimsPerMonth(),
                indicators.getDaysWithMultipleClaims(), indicators.getPercentDaysWithMultipleClaims(),
                indicators.getMostFrequentActePSName(), indicators.getMostFrequentActePSCount(),
                indicators.getMostFrequentActePSPercent(),
                indicators.getAverageAmountPerClaim().toString());
    }
}
