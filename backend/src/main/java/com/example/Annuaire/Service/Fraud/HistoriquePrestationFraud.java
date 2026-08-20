package com.example.Annuaire.Service.Fraud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.FraudAlert;
import com.example.Annuaire.Models.Remboursement;
import com.example.Annuaire.Repository.RemboursementRepository;
import com.example.Annuaire.Service.HistoricalReimbursementRateService;

@Service
public class HistoriquePrestationFraud {

    @Autowired
    private HistoricalReimbursementRateService rateService;

    @Autowired
    private RemboursementRepository remboursementRepository;

    public List<FraudAlert> detectFraud(Remboursement remboursement) {
        List<FraudAlert> alerts = new ArrayList<>();
        detectRateRelatedFraud(remboursement).ifPresent(alerts::add);
        return alerts;
    }

    private Optional<FraudAlert> detectRateRelatedFraud(Remboursement remboursement) {
        Long formuleId = getFormuleIdForRemboursement(remboursement);
        Long prestationId = getPrestationIdForRemboursement(remboursement);
        LocalDate serviceDate = remboursement.getMovement().getDate().toLocalDate();

        Integer appliedPercentage = remboursement.getReimbursementPercentage();

        Optional<BigDecimal> correctRateOpt = rateService.findRateForDate(formuleId, prestationId, serviceDate);

        if (correctRateOpt.isPresent()) {
            BigDecimal correctRate = correctRateOpt.get();
            BigDecimal appliedRate = BigDecimal.valueOf(appliedPercentage);

            if (correctRate.subtract(appliedRate).abs().compareTo(new BigDecimal("0.01")) > 0) {
                return Optional.of(new FraudAlert(
                        "HISTORICAL_RATE_MISMATCH",
                        "Applied rate " + appliedPercentage + "% differs from historical rate " +
                                correctRate + "% for service date " + serviceDate,
                        remboursement.getId()));
            }
        }

        return Optional.empty();
    }
    private Long getFormuleIdForRemboursement(Remboursement remboursement) {
        return remboursement.getMovement().getAdherant().getContract().getFormule().getId();
    }
    private Long getPrestationIdForRemboursement(Remboursement remboursement) {
        return remboursement.getMovement().getActePS().getPrestation().getId();
    }
}
