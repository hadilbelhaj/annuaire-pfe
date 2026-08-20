package com.example.Annuaire.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Annuaire.Models.TransactionStatistics;
import com.example.Annuaire.enums.PeriodType;

public interface TransactionStatisticsRepository
        extends JpaRepository<TransactionStatistics, Long> {
    TransactionStatistics findByPeriodAndPeriodType(String period, PeriodType periodType);

    boolean existsByPeriodAndPeriodType(String period, PeriodType periodType);
}
