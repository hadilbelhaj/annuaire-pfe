package com.example.DTOS.MouvementParPs;

import java.math.BigDecimal;

public class MonthlyStatsDTO {
    private String month;
    private int year;
    private int transactionCount;
    private BigDecimal totalAmount;

    // Constructors
    public MonthlyStatsDTO() {
    }

    public MonthlyStatsDTO(String month, int year, int transactionCount, BigDecimal totalAmount) {
        this.month = month;
        this.year = year;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public void setTransactionCount(int transactionCount) {
        this.transactionCount = transactionCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
