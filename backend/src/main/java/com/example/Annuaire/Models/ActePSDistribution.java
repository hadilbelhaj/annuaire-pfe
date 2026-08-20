package com.example.Annuaire.Models;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class ActePSDistribution {

    private String actePS;
    private Long count;

    public ActePSDistribution(String actePS, Long count) {
        this.actePS = actePS;
        this.count = count;
    }
}
