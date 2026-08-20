package com.example.Annuaire.Models;

import org.springframework.stereotype.Component;

import com.example.DTOS.HealthcareProfessionalDTO;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.ps.ActePSDTO;
import com.example.DTOS.ps.PrestationDTO;

@Component
public class MovementMapper {

    public MovementDTO toDTO(Movement movement) {
        MovementDTO dto = new MovementDTO();
        dto.setId(movement.getId());
        dto.setDate(movement.getDate());
        dto.setAmount(movement.getAmount());

        // Map adherent information
        if (movement.getAdherant() != null) {
            dto.setAdherentName(movement.getAdherant().getName());
            dto.setAdherentId(movement.getAdherant().getId());
        }

        // Map healthcare professional information
        if (movement.getHealthcareProfessional() != null) {
            HealthcareProfessionalDTO hpDTO = new HealthcareProfessionalDTO();
            hpDTO.setId(movement.getHealthcareProfessional().getId());
            hpDTO.setName(movement.getHealthcareProfessional().getName());
            hpDTO.setMedicalSpecialty(movement.getHealthcareProfessional().getmedicalSpecialty());
            dto.setHealthcareProfessional(hpDTO);
        }

        // Map ActePS and Prestation information
        if (movement.getActePS() != null) {
            ActePSDTO actePSDTO = new ActePSDTO();

            if (movement.getActePS().getPrestation() != null) {
                PrestationDTO prestationDTO = new PrestationDTO();
                prestationDTO.setPrestation_libelle(movement.getActePS().getPrestation().getPrestation_libelle());
                actePSDTO.setPrestation(prestationDTO);
            }

            dto.setActePS(actePSDTO);
        }

        return dto;
    }
}