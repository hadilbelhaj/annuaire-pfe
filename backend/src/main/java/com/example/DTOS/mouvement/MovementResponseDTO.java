package com.example.DTOS.mouvement;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovementResponseDTO {
    private Long movementId;
    private Long doctorId;
    private String doctorName;
    private String medicalSpecialty;
    private Long adherantId;
    private String adherantName;
    private Long adherantDeductible;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
    private BigDecimal specialtyAverageAmount;
}
