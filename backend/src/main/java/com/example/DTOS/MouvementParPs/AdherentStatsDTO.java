package com.example.DTOS.MouvementParPs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AdherentStatsDTO {
    private Long adherentId;
    private String adherentName;
    private int transactionCount;
    private BigDecimal totalAmount;
    private LocalDateTime lastTransactionDate;

    // Constructors
    public AdherentStatsDTO() {
    }

    public AdherentStatsDTO(Long adherentId, String adherentName, int transactionCount, BigDecimal totalAmount,
            LocalDateTime lastTransactionDate) {
        this.adherentId = adherentId;
        this.adherentName = adherentName;
        this.transactionCount = transactionCount;
        this.totalAmount = totalAmount;
        this.lastTransactionDate = lastTransactionDate;
    }

    // Getters and Setters
    public Long getAdherentId() {
        return adherentId;
    }

    public void setAdherentId(Long adherentId) {
        this.adherentId = adherentId;
    }

    public String getAdherentName() {
        return adherentName;
    }

    public void setAdherentName(String adherentName) {
        this.adherentName = adherentName;
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

    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }

    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
}
