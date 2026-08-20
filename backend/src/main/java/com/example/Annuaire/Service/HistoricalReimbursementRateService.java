package com.example.Annuaire.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.Annuaire.Models.Formule;
import com.example.Annuaire.Models.HistoricalReimbursementRate;
import com.example.Annuaire.Models.Prestation;
import com.example.Annuaire.Repository.FormuleRepository;
import com.example.Annuaire.Repository.HistoricalReimbursementRateRepository;
import com.example.Annuaire.Repository.PrestationRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class HistoricalReimbursementRateService {

    @Autowired
    private HistoricalReimbursementRateRepository rateRepository;

    @Autowired
    private FormuleRepository formuleRepository;

    @Autowired
    private PrestationRepository prestationRepository;

    public Optional<BigDecimal> findRateForDate(Long formuleId, Long prestationId, LocalDate date) {
        return rateRepository.findRateForDate(formuleId, prestationId, date)
                .map(HistoricalReimbursementRate::getPercentage);
    }

    public Optional<BigDecimal> findCurrentRate(Long formuleId, Long prestationId) {
        return rateRepository.findCurrentRate(formuleId, prestationId)
                .map(HistoricalReimbursementRate::getPercentage);
    }

    /**
     * Get rate history for a specific formule and prestation
     */
    public List<HistoricalReimbursementRate> getRateHistory(Long formuleId, Long prestationId) {
        return rateRepository.findByFormuleIdAndPrestationIdOrderByEffectiveFromDesc(formuleId, prestationId);
    }

    /**
     * Update a reimbursement rate, preserving history
     */
    @Transactional
    public HistoricalReimbursementRate updateRate(
            Long formuleId, Long prestationId, BigDecimal newPercentage,
            LocalDate effectiveFrom, String createdBy) {

        // Get formule and prestation entities
        Formule formule = formuleRepository.findById(formuleId)
                .orElseThrow(() -> new IllegalArgumentException("Formule not found"));

        Prestation prestation = prestationRepository.findById(prestationId)
                .orElseThrow(() -> new IllegalArgumentException("Prestation not found"));

        // Close the current rate period if one exists
        Optional<HistoricalReimbursementRate> currentRateOpt = rateRepository.findCurrentRate(formuleId, prestationId);

        if (currentRateOpt.isPresent()) {
            HistoricalReimbursementRate currentRate = currentRateOpt.get();
            currentRate.setEffectiveTo(effectiveFrom.minusDays(1));
            rateRepository.save(currentRate);

            // Also update the FormulePrestation table
            updateFormulePrestation(formuleId, prestationId, newPercentage);
        }

        // Create new rate entry
        HistoricalReimbursementRate newRate = new HistoricalReimbursementRate();
        newRate.setFormule(formule);
        newRate.setPrestation(prestation);
        newRate.setPercentage(newPercentage);
        newRate.setEffectiveFrom(effectiveFrom);
        newRate.setCreatedAt(java.time.LocalDateTime.now());
        newRate.setCreatedBy(createdBy);

        return rateRepository.save(newRate);
    }

    @Transactional
    public void initializeFromCurrentRates(LocalDate effectiveFrom, String createdBy) {

    }

    private void updateFormulePrestation(Long formuleId, Long prestationId, BigDecimal newPercentage) {

    }
}
