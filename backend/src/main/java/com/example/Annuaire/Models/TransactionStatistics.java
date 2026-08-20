package com.example.Annuaire.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.example.Annuaire.enums.PeriodType;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transaction_statistics")
@Getter
@Setter
@NoArgsConstructor
public class TransactionStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String period;

    @Enumerated(EnumType.STRING)
    private PeriodType periodType;
    private BigDecimal totalMonetaryValue;
    private BigDecimal averageTransactionAmount;
    private String highestActivityRegion;
    private String lowestActivityRegion;

    private Long topProfessionalByPatientCount;
    private String topProfessionalByPatientCountName;
    private Long topProfessionalByTransactionAmount;
    private String topProfessionalByTransactionAmountName;
    private Long topProfessionalByAverageValue;
    private String topProfessionalByAverageValueName;

    private BigDecimal monthOverMonthGrowthPercentage;
    @Column(length = 2000)
    private String prestationTypeDistributionJson;
    @Column(length = 4000)
    private String actePSDistributionJson;
    @Column(length = 2000)
    private String prestationFinancialAnalysisJson;

    @Column(columnDefinition = "TEXT")
    private String specialtyTransactionVolumes;

    private LocalDateTime generatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public BigDecimal getTotalMonetaryValue() {
        return totalMonetaryValue;
    }

    public void setTotalMonetaryValue(BigDecimal totalMonetaryValue) {
        this.totalMonetaryValue = totalMonetaryValue;
    }

    public BigDecimal getAverageTransactionAmount() {
        return averageTransactionAmount;
    }

    public void setAverageTransactionAmount(BigDecimal averageTransactionAmount) {
        this.averageTransactionAmount = averageTransactionAmount;
    }

    public String getHighestActivityRegion() {
        return highestActivityRegion;
    }

    public void setHighestActivityRegion(String highestActivityRegion) {
        this.highestActivityRegion = highestActivityRegion;
    }

    public String getLowestActivityRegion() {
        return lowestActivityRegion;
    }

    public void setLowestActivityRegion(String lowestActivityRegion) {
        this.lowestActivityRegion = lowestActivityRegion;
    }

    public Long getTopProfessionalByPatientCount() {
        return topProfessionalByPatientCount;
    }

    public void setTopProfessionalByPatientCount(Long topProfessionalByPatientCount) {
        this.topProfessionalByPatientCount = topProfessionalByPatientCount;
    }

    public String getTopProfessionalByPatientCountName() {
        return topProfessionalByPatientCountName;
    }

    public void setTopProfessionalByPatientCountName(String topProfessionalByPatientCountName) {
        this.topProfessionalByPatientCountName = topProfessionalByPatientCountName;
    }

    public Long getTopProfessionalByTransactionAmount() {
        return topProfessionalByTransactionAmount;
    }

    public void setTopProfessionalByTransactionAmount(Long topProfessionalByTransactionAmount) {
        this.topProfessionalByTransactionAmount = topProfessionalByTransactionAmount;
    }

    public String getTopProfessionalByTransactionAmountName() {
        return topProfessionalByTransactionAmountName;
    }

    public void setTopProfessionalByTransactionAmountName(
            String topProfessionalByTransactionAmountName) {
        this.topProfessionalByTransactionAmountName = topProfessionalByTransactionAmountName;
    }

    public Long getTopProfessionalByAverageValue() {
        return topProfessionalByAverageValue;
    }

    public void setTopProfessionalByAverageValue(Long topProfessionalByAverageValue) {
        this.topProfessionalByAverageValue = topProfessionalByAverageValue;
    }

    public String getTopProfessionalByAverageValueName() {
        return topProfessionalByAverageValueName;
    }

    public void setTopProfessionalByAverageValueName(String topProfessionalByAverageValueName) {
        this.topProfessionalByAverageValueName = topProfessionalByAverageValueName;
    }

    public BigDecimal getMonthOverMonthGrowthPercentage() {
        return monthOverMonthGrowthPercentage;
    }

    public void setMonthOverMonthGrowthPercentage(BigDecimal monthOverMonthGrowthPercentage) {
        this.monthOverMonthGrowthPercentage = monthOverMonthGrowthPercentage;
    }

    public String getSpecialtyTransactionVolumes() {
        return specialtyTransactionVolumes;
    }

    public void setSpecialtyTransactionVolumes(String specialtyTransactionVolumes) {
        this.specialtyTransactionVolumes = specialtyTransactionVolumes;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

}
