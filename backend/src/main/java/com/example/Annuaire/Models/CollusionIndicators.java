package com.example.Annuaire.Models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CollusionIndicators {
    private double claimsPerMonth;
    private long daysWithMultipleClaims;
    private double percentDaysWithMultipleClaims;
    private int mostFrequentActePSCount;
    private Long mostFrequentActePSId;
    private String mostFrequentActePSName;
    private double mostFrequentActePSPercent;
    private BigDecimal totalAmountClaimed;
    private BigDecimal averageAmountPerClaim;
    private boolean suspicious;

   
    public void evaluateSuspiciousness(CollusionThresholds thresholds) {
        suspicious = (claimsPerMonth >= thresholds.getClaimsPerMonthThreshold() ||
                percentDaysWithMultipleClaims >= thresholds.getPercentDaysWithMultipleClaimsThreshold() ||
                mostFrequentActePSPercent >= thresholds.getMostFrequentActePSPercentThreshold());
    }
    public boolean isSuspicious() {
        return suspicious;
    }

}
