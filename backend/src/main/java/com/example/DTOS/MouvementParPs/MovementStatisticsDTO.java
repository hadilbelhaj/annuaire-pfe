package com.example.DTOS.MouvementParPs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MovementStatisticsDTO {
    private int totalMovements;
    private BigDecimal totalAmount;
    private BigDecimal averageAmount;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private LocalDateTime mostRecentTransactionDate;
    private int uniqueAdherentsCount;

    // Constructors
    public MovementStatisticsDTO() {
    }

    public MovementStatisticsDTO(int totalMovements, BigDecimal totalAmount, BigDecimal averageAmount,
            BigDecimal minAmount, BigDecimal maxAmount, LocalDateTime mostRecentTransactionDate,
            int uniqueAdherentsCount) {
        this.totalMovements = totalMovements;
        this.totalAmount = totalAmount;
        this.averageAmount = averageAmount;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.mostRecentTransactionDate = mostRecentTransactionDate;
        this.uniqueAdherentsCount = uniqueAdherentsCount;
    }

    // Getters and Setters
    public int getTotalMovements() {
        return totalMovements;
    }

    public void setTotalMovements(int totalMovements) {
        this.totalMovements = totalMovements;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getAverageAmount() {
        return averageAmount;
    }

    public void setAverageAmount(BigDecimal averageAmount) {
        this.averageAmount = averageAmount;
    }

    public BigDecimal getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(BigDecimal minAmount) {
        this.minAmount = minAmount;
    }

    public BigDecimal getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(BigDecimal maxAmount) {
        this.maxAmount = maxAmount;
    }

    public LocalDateTime getMostRecentTransactionDate() {
        return mostRecentTransactionDate;
    }

    public void setMostRecentTransactionDate(LocalDateTime mostRecentTransactionDate) {
        this.mostRecentTransactionDate = mostRecentTransactionDate;
    }

    public int getUniqueAdherentsCount() {
        return uniqueAdherentsCount;
    }

    public void setUniqueAdherentsCount(int uniqueAdherentsCount) {
        this.uniqueAdherentsCount = uniqueAdherentsCount;
    }
}
