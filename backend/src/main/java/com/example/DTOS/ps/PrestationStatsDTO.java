package com.example.DTOS.ps;

import java.math.BigDecimal;

public interface PrestationStatsDTO {
    Long getPrestationId();

    String getPrestationName();

    Long getFrequency();

    BigDecimal getTotalRevenue();

    BigDecimal getAverageAmount();
}
