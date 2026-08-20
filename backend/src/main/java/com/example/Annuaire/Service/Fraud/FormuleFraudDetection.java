package com.example.Annuaire.Service.Fraud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.Adherant;
import com.example.Annuaire.Models.Formule;
import com.example.Annuaire.Models.FraudAlert;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.Remboursement;
import com.example.Annuaire.Repository.FormuleRepository;
import com.example.Annuaire.Repository.RemboursementRepository;
import com.example.Annuaire.Service.HistoricalReimbursementRateService;

@Service
public class FormuleFraudDetection {

    @Autowired
    private HistoricalReimbursementRateService rateService;

    @Autowired
    private RemboursementRepository remboursementRepository;

    @Autowired
    private FormuleRepository formuleRepository;

    public List<FraudAlert> detectFormuleMismatch(Remboursement remboursement) {
        List<FraudAlert> alerts = new ArrayList<>();
        Long currentFormuleId = getFormuleIdForRemboursement(remboursement);
        String currentFormuleName = remboursement.getMovement().getAdherant().getContract().getFormule()
                .getLibelle_formule();
        Long prestationId = getPrestationIdForRemboursement(remboursement);
        LocalDate serviceDate = remboursement.getMovement().getDate().toLocalDate();

        Integer appliedPercentage = remboursement.getReimbursementPercentage();
        BigDecimal appliedRate = BigDecimal.valueOf(appliedPercentage);

        Optional<BigDecimal> correctRateOpt = rateService.findRateForDate(
                currentFormuleId, prestationId, serviceDate);

        if (!correctRateOpt.isPresent()) {
            return alerts;
        }

        BigDecimal correctRate = correctRateOpt.get();

        List<Formule> allFormules = formuleRepository.findAll();

        for (Formule formule : allFormules) {
            if (formule.getId().equals(currentFormuleId)) {
                continue;
            }

            Optional<BigDecimal> otherFormuleRate = rateService.findRateForDate(
                    formule.getId(), prestationId, serviceDate);

            if (otherFormuleRate.isPresent() &&
                    otherFormuleRate.get().compareTo(appliedRate) == 0) {

                FraudAlert alert = createFormuleMismatchAlert(
                        remboursement,
                        formule.getId(),
                        formule.getLibelle_formule(),
                        currentFormuleId,
                        currentFormuleName,
                        appliedPercentage,
                        correctRate);

                alerts.add(alert);
            }
        }

        return alerts;
    }

    private FraudAlert createFormuleMismatchAlert(
            Remboursement remboursement,
            Long wrongFormuleId,
            String wrongFormuleName,
            Long correctFormuleId,
            String correctFormuleName,
            Integer appliedPercentage,
            BigDecimal correctRate) {

        String description = String.format(
                "Applied rate %d%% matches formule '%s'  instead of adherant's correct formule '%s'  and actual rate should be %s%%",
                appliedPercentage,
                wrongFormuleName,
                correctFormuleName,
                correctRate.toString());

        return new FraudAlert(
                "FORMULE_MISMATCH",
                description,
                remboursement.getId());
    }

    private Long getFormuleIdForRemboursement(Remboursement remboursement) {
        return remboursement.getMovement().getAdherant().getContract().getFormule().getId();
    }

    private Long getPrestationIdForRemboursement(Remboursement remboursement) {
        return remboursement.getMovement().getActePS().getPrestation().getId();
    }
}
