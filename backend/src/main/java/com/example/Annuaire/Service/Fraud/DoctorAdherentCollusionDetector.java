package com.example.Annuaire.Service.Fraud;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.Adherant;
import com.example.Annuaire.Models.CollusionFlag;
import com.example.Annuaire.Models.CollusionIndicators;
import com.example.Annuaire.Models.CollusionThresholds;
import com.example.Annuaire.Models.DoctorAdherentPair;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;

@Service
public class DoctorAdherentCollusionDetector {

    public List<CollusionFlag> detectCollusion(
            List<Movement> movements,
            CollusionThresholds thresholds) {

        List<CollusionFlag> flags = new ArrayList<>();

        // Group movements by doctor-adherent pair
        Map<DoctorAdherentPair, List<Movement>> pairMovements = movements.stream()
                .collect(Collectors.groupingBy(movement -> new DoctorAdherentPair(
                        movement.getHealthcareProfessional().getId(),
                        movement.getAdherant().getId())));

        // Analyze each doctor-adherent relationship
        for (Map.Entry<DoctorAdherentPair, List<Movement>> entry : pairMovements.entrySet()) {
            DoctorAdherentPair pair = entry.getKey();
            List<Movement> pairHistory = entry.getValue();

            // Skip if not enough movements to be suspicious
            if (pairHistory.size() < thresholds.getMinMovementsForSuspicion()) {
                continue;
            }

            // Sort movements by date
            pairHistory.sort(Comparator.comparing(Movement::getDate));

            // Check for indicators of collusion
            CollusionIndicators indicators = analyzeClaimingPattern(pairHistory, thresholds);

            // If any indicators exceed thresholds, flag the pair
            if (indicators.isSuspicious()) {
                HealthcareProfessional doctor = pairHistory.get(0).getHealthcareProfessional();
                Adherant adherent = pairHistory.get(0).getAdherant();

                flags.add(new CollusionFlag(
                        pair,
                        doctor.getName(),
                        adherent.getFirstName() + " " + adherent.getLastName(),
                        indicators,
                        calculateRiskScore(indicators)));
            }
        }

        // Sort flags by risk score (highest first)
        flags.sort(Comparator.comparing(CollusionFlag::getRiskScore).reversed());

        return flags;
    }

    /**
     * Analyzes the claiming pattern between a doctor and adherent to identify
     * suspicious indicators of collusion.
     */
    private CollusionIndicators analyzeClaimingPattern(
            List<Movement> movements,
            CollusionThresholds thresholds) {

        CollusionIndicators indicators = new CollusionIndicators();

        // If movements list is empty, return empty indicators
        if (movements.isEmpty()) {
            return indicators;
        }

        // Calculate time span from first to last movement
        LocalDateTime firstDate = movements.get(0).getDate();
        LocalDateTime lastDate = movements.get(movements.size() - 1).getDate();
        long daysBetween = ChronoUnit.DAYS.between(firstDate, lastDate);

        // Avoid division by zero
        double daysOrOne = Math.max(1.0, daysBetween);

        // Calculate frequency (claims per month)
        double monthsBetween = daysOrOne / 30.0;
        double claimsPerMonth = movements.size() / monthsBetween;
        indicators.setClaimsPerMonth(claimsPerMonth);

        // Look for same-day claims
        Map<LocalDateTime, List<Movement>> movementsByDate = movements.stream()
                .collect(Collectors.groupingBy(m -> m.getDate().truncatedTo(ChronoUnit.DAYS)));

        // Count days with multiple claims
        long daysWithMultipleClaims = movementsByDate.values().stream()
                .filter(dailyMovements -> dailyMovements.size() > 1)
                .count();
        indicators.setDaysWithMultipleClaims(daysWithMultipleClaims);

        // Calculate percentage of days with multiple claims
        long uniqueDaysWithClaims = movementsByDate.size();
        double percentDaysMultipleClaims = (double) daysWithMultipleClaims / uniqueDaysWithClaims * 100.0;
        indicators.setPercentDaysWithMultipleClaims(percentDaysMultipleClaims);

        // Analyze ActePS repetition patterns
        Map<Long, List<Movement>> movementsByActePS = movements.stream()
                .collect(Collectors.groupingBy(m -> m.getActePS() != null ? m.getActePS().getId() : -1L));

        // Find the most frequently claimed ActePS
        Optional<Map.Entry<Long, List<Movement>>> mostFrequentActePS = movementsByActePS.entrySet().stream()
                .max(Comparator.comparingInt(e -> e.getValue().size()));

        if (mostFrequentActePS.isPresent() && mostFrequentActePS.get().getKey() != -1L) {
            Long acteId = mostFrequentActePS.get().getKey();
            List<Movement> acteMovements = mostFrequentActePS.get().getValue();

            // Number of times this ActePS was claimed
            indicators.setMostFrequentActePSCount(acteMovements.size());
            indicators.setMostFrequentActePSId(acteId);
            indicators.setMostFrequentActePSName(acteMovements.get(0).getActePS().getLibelle_actePs());

            // Calculate what percentage of all claims is this ActePS
            double percentOfMostFrequentActe = (double) acteMovements.size() / movements.size() * 100.0;
            indicators.setMostFrequentActePSPercent(percentOfMostFrequentActe);
        }

        // Calculate total amount claimed in this relationship
        BigDecimal totalAmount = movements.stream()
                .map(Movement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        indicators.setTotalAmountClaimed(totalAmount);

        // Average amount per claim
        BigDecimal avgAmount = totalAmount.divide(
                new BigDecimal(movements.size()),
                2,
                BigDecimal.ROUND_HALF_UP);
        indicators.setAverageAmountPerClaim(avgAmount);

        // Set whether these indicators are suspicious according to thresholds
        indicators.evaluateSuspiciousness(thresholds);

        return indicators;
    }

    /**
     * Calculate a risk score (0-100) based on the indicators
     */
    private int calculateRiskScore(CollusionIndicators indicators) {
        // This is a simplified scoring model - in production this would be more
        // sophisticated
        int score = 0;

        // High frequency of claims increases risk
        if (indicators.getClaimsPerMonth() > 5) {
            score += 20;
        } else if (indicators.getClaimsPerMonth() > 2) {
            score += 10;
        }

        // Multiple claims on same day is suspicious
        if (indicators.getPercentDaysWithMultipleClaims() > 50) {
            score += 30;
        } else if (indicators.getPercentDaysWithMultipleClaims() > 20) {
            score += 15;
        }

        // High concentration of the same ActePS is suspicious
        if (indicators.getMostFrequentActePSPercent() > 80) {
            score += 25;
        } else if (indicators.getMostFrequentActePSPercent() > 50) {
            score += 15;
        }

        // High average amounts increase risk
        if (indicators.getAverageAmountPerClaim().compareTo(new BigDecimal("500")) > 0) {
            score += 25;
        } else if (indicators.getAverageAmountPerClaim().compareTo(new BigDecimal("200")) > 0) {
            score += 10;
        }

        return Math.min(100, score); // Cap at 100
    }
}
