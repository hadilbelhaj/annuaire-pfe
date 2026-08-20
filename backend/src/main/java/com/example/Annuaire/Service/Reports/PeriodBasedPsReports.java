package com.example.Annuaire.Service.Reports;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.enums.PeriodType;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.TransactionStatistics;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Repository.TransactionStatisticsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

@Service
public class PeriodBasedPsReports {

    private final MovementRepository movementRepository;
    private final HealthcareProfessionalRepository healthcareProfessionalRepository;
    private final TransactionStatisticsRepository statisticsRepository;

    @Autowired
    public PeriodBasedPsReports(MovementRepository movementRepository,
            HealthcareProfessionalRepository healthcareProfessionalRepository,
            TransactionStatisticsRepository statisticsRepository) {
        this.movementRepository = movementRepository;
        this.healthcareProfessionalRepository = healthcareProfessionalRepository;
        this.statisticsRepository = statisticsRepository;
    }

    @Transactional
    public TransactionStatistics generateMonthlyStatistics(int year, int month) {
        // Format period string (e.g., "2025-03")
        String period = String.format("%d-%02d", year, month);
        LocalDateTime startOfPeriod = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime endOfPeriod = startOfPeriod.plusMonths(1).minusNanos(1);

        return generateStatistics(period, PeriodType.MONTH, startOfPeriod, endOfPeriod);
    }

    @Transactional
    public TransactionStatistics generateQuarterlyStatistics(int year, int quarter) {
        if (quarter < 1 || quarter > 4) {
            throw new IllegalArgumentException("Quarter must be between 1 and 4");
        }
        String period = String.format("%d-Q%d", year, quarter);
        int startMonth = (quarter - 1) * 3 + 1;
        LocalDateTime startOfPeriod = LocalDateTime.of(year, startMonth, 1, 0, 0);
        LocalDateTime endOfPeriod = startOfPeriod.plusMonths(3).minusNanos(1);

        return generateStatistics(period, PeriodType.QUARTER, startOfPeriod, endOfPeriod);
    }

    @Transactional
    public TransactionStatistics generateYearlyStatistics(int year) {
        String period = String.valueOf(year);
        LocalDateTime startOfPeriod = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfPeriod = startOfPeriod.plusYears(1).minusNanos(1);

        return generateStatistics(period, PeriodType.YEAR, startOfPeriod, endOfPeriod);
    }

    private TransactionStatistics generateStatistics(
            String period, PeriodType periodType,
            LocalDateTime startOfPeriod, LocalDateTime endOfPeriod) {

        List<Movement> movements = movementRepository.findByDateBetween(startOfPeriod, endOfPeriod);

        Map<String, Long> prestationTypeDistribution = movements.stream()
                .filter(m -> m.getActePS() != null && m.getActePS().getPrestation() != null)
                .map(m -> m.getActePS().getPrestation().getPrestation_libelle().toString().toUpperCase())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Long> actePSDistribution = movements.stream()
                .filter(m -> m.getActePS() != null)
                .map(m -> m.getActePS().getLibelle_actePs())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, BigDecimal> prestationFinancialAnalysis = movements.stream()
                .filter(m -> m.getActePS() != null && m.getActePS().getPrestation() != null)
                .collect(Collectors.groupingBy(
                        m -> m.getActePS().getPrestation().getPrestation_libelle().toString(),
                        Collectors.mapping(Movement::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));
        ObjectMapper mapper1 = new ObjectMapper();
        String prestationTypeJson, actePSJson, financialAnalysisJson;
        try {
            prestationTypeJson = mapper1.writeValueAsString(prestationTypeDistribution);
            actePSJson = mapper1.writeValueAsString(actePSDistribution);
            financialAnalysisJson = mapper1.writeValueAsString(prestationFinancialAnalysis);
        } catch (Exception e) {
            prestationTypeJson = "{}";
            actePSJson = "{}";
            financialAnalysisJson = "{}";
        }

        BigDecimal totalValue = movements.stream()
                .map(Movement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avgAmount = movements.isEmpty()
                ? BigDecimal.ZERO
                : totalValue.divide(BigDecimal.valueOf(movements.size()), RoundingMode.HALF_UP);

        Map<String, Long> regionActivityCount = movements.stream()
                .filter(m -> m.getHealthcareProfessional() != null && m.getHealthcareProfessional().getRegion() != null)
                .map(m -> m.getHealthcareProfessional().getRegion())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Find highest/lowest activity regions
        String highestRegion = regionActivityCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        String lowestRegion = regionActivityCount.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("Unknown");

        // Generate transactions by specialty
        Map<String, Long> specialtyTransactions = movements.stream()
                .map(m -> m.getHealthcareProfessional().getmedicalSpecialty())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        // Convert specialty transactions to JSON
        ObjectMapper mapper = new ObjectMapper();
        String specialtyJson;
        try {
            specialtyJson = mapper.writeValueAsString(specialtyTransactions);
        } catch (Exception e) {
            specialtyJson = "{}";
        }

        // Top professionals by patient count
        Map<HealthcareProfessional, Long> professionalPatientCount = movements.stream()
                .collect(Collectors.groupingBy(Movement::getHealthcareProfessional,
                        Collectors.mapping(Movement::getAdherant, Collectors.counting())));

        Map.Entry<HealthcareProfessional, Long> topByPatientCount = professionalPatientCount.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        // Top professionals by transaction amount
        Map<HealthcareProfessional, BigDecimal> professionalAmounts = movements.stream()
                .collect(Collectors.groupingBy(Movement::getHealthcareProfessional,
                        Collectors.mapping(Movement::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))));

        Map.Entry<HealthcareProfessional, BigDecimal> topByAmount = professionalAmounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        // Top professionals by average transaction value
        Map<HealthcareProfessional, Double> professionalAvgValues = movements.stream()
                .collect(Collectors.groupingBy(Movement::getHealthcareProfessional,
                        Collectors.averagingDouble(m -> m.getAmount().doubleValue())));

        Map.Entry<HealthcareProfessional, Double> topByAvgValue = professionalAvgValues.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        BigDecimal previousPeriodTotal = calculatePreviousPeriodTotal(period, periodType);
        BigDecimal growthPercentage = BigDecimal.ZERO;

        if (previousPeriodTotal.compareTo(BigDecimal.ZERO) > 0) {
            growthPercentage = totalValue.subtract(previousPeriodTotal)
                    .divide(previousPeriodTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100"));
        }

        TransactionStatistics statistics = new TransactionStatistics();
        statistics.setPeriod(period);
        statistics.setPeriodType(periodType);
        statistics.setTotalMonetaryValue(totalValue);
        statistics.setAverageTransactionAmount(avgAmount);
        statistics.setHighestActivityRegion(highestRegion);
        statistics.setLowestActivityRegion(lowestRegion);
        statistics.setSpecialtyTransactionVolumes(specialtyJson);
        statistics.setPrestationTypeDistributionJson(prestationTypeJson);
        statistics.setActePSDistributionJson(actePSJson);
        statistics.setPrestationFinancialAnalysisJson(financialAnalysisJson);

        if (topByPatientCount != null) {
            statistics.setTopProfessionalByPatientCount(topByPatientCount.getKey().getId());
            statistics.setTopProfessionalByPatientCountName(topByPatientCount.getKey().getName());
        }

        if (topByAmount != null) {
            statistics.setTopProfessionalByTransactionAmount(topByAmount.getKey().getId());
            statistics.setTopProfessionalByTransactionAmountName(topByAmount.getKey().getName());
        }

        if (topByAvgValue != null) {
            statistics.setTopProfessionalByAverageValue(topByAvgValue.getKey().getId());
            statistics.setTopProfessionalByAverageValueName(topByAvgValue.getKey().getName());
        }

        statistics.setMonthOverMonthGrowthPercentage(growthPercentage);
        statistics.setGeneratedAt(LocalDateTime.now());

        return statisticsRepository.save(statistics);
    }

    public TransactionStatistics findByPeriodAndType(String period, PeriodType periodType) {
        return statisticsRepository.findByPeriodAndPeriodType(period, periodType);
    }

    private BigDecimal calculatePreviousPeriodTotal(String period, PeriodType periodType) {
        String previousPeriod;
        PeriodType previousPeriodType = periodType;

        switch (periodType) {
            case MONTH:
                // Parse current period (e.g., "2025-03")
                String[] parts = period.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                // Calculate previous month
                LocalDate current = LocalDate.of(year, month, 1);
                LocalDate previous = current.minusMonths(1);
                previousPeriod = String.format("%d-%02d", previous.getYear(), previous.getMonthValue());
                break;

            case QUARTER:
                // Parse current period (e.g., "2025-Q1")
                parts = period.split("-Q");
                year = Integer.parseInt(parts[0]);
                int quarter = Integer.parseInt(parts[1]);

                // Calculate previous quarter
                if (quarter > 1) {
                    previousPeriod = String.format("%d-Q%d", year, quarter - 1);
                } else {
                    previousPeriod = String.format("%d-Q4", year - 1);
                }
                break;

            case YEAR:
                // Parse current period (e.g., "2025")
                year = Integer.parseInt(period);
                previousPeriod = String.valueOf(year - 1);
                break;

            default:
                throw new IllegalArgumentException("Unknown period type: " + periodType);
        }

        // Check if previous period statistics exist
        TransactionStatistics prevStats = statisticsRepository.findByPeriodAndPeriodType(
                previousPeriod, previousPeriodType);

        if (prevStats != null) {
            return prevStats.getTotalMonetaryValue();
        } else {
            // Calculate on-the-fly if previous period stats don't exist
            return calculateTotalForPeriod(previousPeriod, previousPeriodType);
        }
    }

    private BigDecimal calculateTotalForPeriod(String period, PeriodType periodType) {
        LocalDateTime startOfPeriod, endOfPeriod;

        switch (periodType) {
            case MONTH:
                // Parse period (e.g., "2025-03")
                String[] parts = period.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                startOfPeriod = LocalDateTime.of(year, month, 1, 0, 0);
                endOfPeriod = startOfPeriod.plusMonths(1).minusNanos(1);
                break;

            case QUARTER:
                // Parse period (e.g., "2025-Q1")
                parts = period.split("-Q");
                year = Integer.parseInt(parts[0]);
                int quarter = Integer.parseInt(parts[1]);
                int startMonth = (quarter - 1) * 3 + 1;

                startOfPeriod = LocalDateTime.of(year, startMonth, 1, 0, 0);
                endOfPeriod = startOfPeriod.plusMonths(3).minusNanos(1);
                break;

            case YEAR:
                // Parse period (e.g., "2025")
                year = Integer.parseInt(period);

                startOfPeriod = LocalDateTime.of(year, 1, 1, 0, 0);
                endOfPeriod = startOfPeriod.plusYears(1).minusNanos(1);
                break;

            default:
                throw new IllegalArgumentException("Unknown period type: " + periodType);
        }

        List<Movement> movements = movementRepository.findByDateBetween(startOfPeriod, endOfPeriod);
        return movements.stream()
                .map(Movement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean checkReportExists(String period, PeriodType periodType) {
        // Query the database to see if a report with this period and type exists
        return statisticsRepository.existsByPeriodAndPeriodType(period, periodType);
    }
}
