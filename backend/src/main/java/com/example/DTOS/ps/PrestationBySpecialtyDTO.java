package com.example.DTOS.ps;

import java.math.BigDecimal;

public interface PrestationBySpecialtyDTO {
    String getSpecialty();

    Long getPrestationId();

    String getPrestationName();

    Long getFrequency();

    BigDecimal getTotalRevenue();
}
