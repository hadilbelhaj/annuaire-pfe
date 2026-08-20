package com.example.DTOS.ps;

import com.example.Annuaire.Models.Prestation;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ActePSDTO {
    private PrestationDTO prestation;

    public ActePSDTO() {
    }

    public PrestationDTO getPrestation() {
        return prestation;
    }

    public void setPrestation(PrestationDTO prestation) {
        this.prestation = prestation;
    }
}