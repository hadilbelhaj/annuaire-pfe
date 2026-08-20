package com.example.DTOS.ps;

import com.example.Annuaire.enums.Enums.Prestation_libelle;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrestationDTO {
    private Prestation_libelle prestation_libelle;

    public PrestationDTO() {
    }

}
