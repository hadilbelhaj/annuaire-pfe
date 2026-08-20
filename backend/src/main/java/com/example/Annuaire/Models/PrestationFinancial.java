package com.example.Annuaire.Models;

import java.math.BigDecimal;
import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class PrestationFinancial {
    private String prestationType;
    private BigDecimal amount;

    public PrestationFinancial(String prestationType, BigDecimal amount) {
        this.prestationType = prestationType;
        this.amount = amount;
    }
}
