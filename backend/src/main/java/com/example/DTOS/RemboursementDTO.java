package com.example.DTOS;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class RemboursementDTO {
    private Long movementId;
    private Long doctorId;
    private String doctorName;
    private String medicalSpecialty;
    private Long adherantId;
    private String adherantName;
    private Long adherantDeductible;
    private BigDecimal amount;
    private LocalDateTime date;
    private BigDecimal specialtyAverageAmount;
    private String designation;
    private Integer reimbursementPercentage;
    private BigDecimal insuranceAmount;
    private BigDecimal adherantAmount;

}
