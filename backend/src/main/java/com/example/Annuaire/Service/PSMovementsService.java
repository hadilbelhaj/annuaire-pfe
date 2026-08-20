package com.example.Annuaire.Service;

import com.example.Annuaire.Models.ActePS;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.ActePsRepository;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.DTOS.HealthcareProfessionalDTO;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.ps.ActePSDTO;
import com.example.DTOS.ps.PrestationDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class PSMovementsService {

    private final HealthcareProfessionalRepository healthcareProfessionalRepository;
    private final ActePsRepository actePSRepository;
    private final MovementRepository movementRepository;

    @Autowired
    public PSMovementsService(
            HealthcareProfessionalRepository healthcareProfessionalRepository,
            ActePsRepository actePSRepository,
            MovementRepository movementRepository) {
        this.healthcareProfessionalRepository = healthcareProfessionalRepository;
        this.actePSRepository = actePSRepository;
        this.movementRepository = movementRepository;
    }

    public List<MovementDTO> getMovementDTOsByPSId(Long psId) {
        Optional<HealthcareProfessional> professionalOpt = healthcareProfessionalRepository.findById(psId);

        if (!professionalOpt.isPresent()) {
            return Collections.emptyList();
        }

        HealthcareProfessional professional = professionalOpt.get();

        List<ActePS> actesPSList = actePSRepository.findByHealthcareProfessional(professional);

        if (actesPSList.isEmpty()) {
            return Collections.emptyList();
        }

        // Step 3: Find all movements for these ActePS entries and convert to DTOs
        List<MovementDTO> movementDTOs = new ArrayList<>();

        for (ActePS actePS : actesPSList) {
            List<Movement> movements = movementRepository.findByActePS(actePS);

            for (Movement movement : movements) {
                MovementDTO dto = new MovementDTO();
                dto.setId(movement.getId());
                dto.setDate(movement.getDate());
                dto.setAmount(movement.getAmount());

                // Use the requested healthcare professional instead of the one from the
                // movement

                HealthcareProfessionalDTO psDTO = new HealthcareProfessionalDTO(
                        professional.getId(),
                        professional.getName(),
                        professional.getmedicalSpecialty());
                dto.setHealthcareProfessional(psDTO);

                // Set the actePS with prestation
                if (movement.getActePS() != null) {
                    ActePSDTO actePSDTO = new ActePSDTO();

                    if (movement.getActePS().getPrestation() != null) {
                        PrestationDTO prestationDTO = new PrestationDTO();
                        prestationDTO
                                .setPrestation_libelle(movement.getActePS().getPrestation().getPrestation_libelle());
                        actePSDTO.setPrestation(prestationDTO);
                    }

                    dto.setActePS(actePSDTO);
                }

                movementDTOs.add(dto);
            }
        }

        return movementDTOs;
    }

    public List<MovementDTO> getMovementDTOsByPSName(String psName) {
        Optional<HealthcareProfessional> professionalOpt = healthcareProfessionalRepository.findByName(psName);

        if (!professionalOpt.isPresent()) {
            return Collections.emptyList();
        }
        return getMovementDTOsByPSId(professionalOpt.get().getId());
    }
}