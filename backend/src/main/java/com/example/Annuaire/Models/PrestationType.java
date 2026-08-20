package com.example.Annuaire.Models;

import lombok.*;

@NoArgsConstructor
@Setter
@Getter
public class PrestationType {
    private String prestationType;
    private Long count;

    public PrestationType(String prestationType, Long count) {
        this.prestationType = prestationType;
        this.count = count;
    }

}
