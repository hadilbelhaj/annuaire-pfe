package com.example.Annuaire.Models;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AdherantsPerPs {
    private LocalDateTime date;
    private String adherentName;
    private String description;
    private BigDecimal amount;

    public AdherantsPerPs(BigDecimal amount, LocalDateTime date, String description, String adherentName) {
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.adherentName = adherentName;
    }

}
