package com.example.Annuaire.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Annuaire.Models.HistoricalReimbursementRate;
    @Repository
    public interface HistoricalReimbursementRateRepository extends JpaRepository<HistoricalReimbursementRate, Long> {

        @Query("SELECT h FROM HistoricalReimbursementRate h " +
                "WHERE h.formule.id = :formuleId " +
                "AND h.prestation.id = :prestationId " +
                "AND h.effectiveFrom <= :date " +
                "AND (h.effectiveTo IS NULL OR h.effectiveTo >= :date)")
        Optional<HistoricalReimbursementRate> findRateForDate(
                @Param("formuleId") Long formuleId,
                @Param("prestationId") Long prestationId,
                @Param("date") LocalDate date);

        @Query("SELECT h FROM HistoricalReimbursementRate h " +
                "WHERE h.formule.id = :formuleId " +
                "AND h.prestation.id = :prestationId " +
                "AND h.effectiveTo IS NULL")
        Optional<HistoricalReimbursementRate> findCurrentRate(
                @Param("formuleId") Long formuleId,
                @Param("prestationId") Long prestationId);

        List<HistoricalReimbursementRate> findByFormuleIdAndPrestationIdOrderByEffectiveFromDesc(
                Long formuleId, Long prestationId);
    }

