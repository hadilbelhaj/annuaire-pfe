package com.example.DTOS.ps;

import java.math.BigDecimal;

public interface PrestationMonthlyStatsDTO {
    Long getPrestationId();

    String getPrestationName();

    Integer getYear();

    Integer getMonth();

    Long getFrequency();

    BigDecimal getTotalRevenue();

    BigDecimal getAverageAmount();
}
