package com.example.Annuaire.Controllers.Fraud;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Service.PSMovementsService;
import com.example.Annuaire.Service.Fraud.prestationDetectionService;
import com.example.DTOS.HealthcareProfessionalDTO;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.ps.ActePSDTO;
import com.example.DTOS.ps.PrestationDTO;

import lombok.Data;

@RestController
@RequestMapping("/api/fraud-prestation")
public class prestationFraudController {
    @Autowired
    private prestationDetectionService fraudDetectionService;
    @Autowired
    private final MovementRepository mvrep;
    @Autowired
    private final PSMovementsService psMovementsService;

    public prestationFraudController(MovementRepository mvrep, PSMovementsService psMovementsService) {
        this.mvrep = mvrep;
        this.psMovementsService = psMovementsService;
    }

    @PostMapping("/check")
    public ResponseEntity<FraudCheckResponse> checkMovement(@RequestBody Movement movement) {
        boolean isFraudulent = fraudDetectionService.isFraudulentMovement(movement);

        FraudCheckResponse response = new FraudCheckResponse();
        response.setMovementId(movement.getId());
        response.setFraudulent(isFraudulent);

        if (isFraudulent) {
            response.setReason("Healthcare professional with specialty '" +
                    movement.getHealthcareProfessional().getmedicalSpecialty() +
                    "' is not authorized to perform prestation '" +
                    movement.getActePS().getPrestation().getPrestation_libelle() + "'");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-all")
    public ResponseEntity<List<MovementDTO>> checkAllMovements() {
        List<MovementDTO> fraudulentMovements = new ArrayList<>();
        List<Movement> movements = mvrep.findAll();

        for (Movement movement : movements) {
            boolean isFraudulent = fraudDetectionService.isFraudulentMovement(movement);

            if (isFraudulent) {
                MovementDTO dto = new MovementDTO();
                dto.setId(movement.getId());
                dto.setAmount(movement.getAmount());
                dto.setDate(movement.getDate());
                dto.setDescription("FRAUDULENT: Healthcare professional with specialty '" +
                        movement.getHealthcareProfessional().getmedicalSpecialty() +
                        "' is not authorized to perform prestation '" +
                        movement.getActePS().getPrestation().getPrestation_libelle() + "'");

                if (movement.getHealthcareProfessional() != null) {
                    dto.setHealthcareProfessionalName(movement.getHealthcareProfessional().getName());

                    HealthcareProfessionalDTO psDTO = new HealthcareProfessionalDTO(
                            movement.getHealthcareProfessional().getId(),
                            movement.getHealthcareProfessional().getName(),
                            movement.getHealthcareProfessional().getmedicalSpecialty());
                    dto.setHealthcareProfessional(psDTO);
                }
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
                if (movement.getAdherant() != null) {
                    dto.setAdherentId(movement.getAdherant().getId());
                    dto.setAdherentName(movement.getAdherant().getName());
                }

                fraudulentMovements.add(dto);
            }
        }

        return ResponseEntity.ok(fraudulentMovements);
    }

    @GetMapping("/check-by-doctor/{doctorName}")
    public ResponseEntity<List<MovementDTO>> checkMovementsByDoctor(@PathVariable String doctorName) {
        List<MovementDTO> fraudulentMovements = new ArrayList<>();

        List<MovementDTO> allMovementDTOs = psMovementsService.getMovementDTOsByPSName(doctorName);
        for (MovementDTO movementDTO : allMovementDTOs) {
            Movement movement = mvrep.findById(movementDTO.getId())
                    .orElse(null);

            if (movement != null) {
                boolean isFraudulent = fraudDetectionService.isFraudulentMovement(movement);

                if (isFraudulent) {
                    MovementDTO fraudDTO = new MovementDTO();
                    fraudDTO.setId(movementDTO.getId());
                    fraudDTO.setAmount(movementDTO.getAmount());
                    fraudDTO.setDate(movementDTO.getDate());
                    fraudDTO.setHealthcareProfessional(movementDTO.getHealthcareProfessional());
                    fraudDTO.setActePS(movementDTO.getActePS());

                    String fraudReason = "Healthcare professional with specialty '" +
                            movement.getActePS().getHealthcareProfessional().getmedicalSpecialty() +
                            "' is not authorized to perform prestation '" +
                            movement.getActePS().getPrestation().getPrestation_libelle() + "'";

                    fraudDTO.setDescription("FRAUDULENT: " + fraudReason);
                    if (movement.getHealthcareProfessional() != null) {
                        fraudDTO.setHealthcareProfessionalName(movement.getHealthcareProfessional().getName());
                    }
                    if (movement.getActePS() != null) {
                        fraudDTO.setActePSName(movement.getActePS().getLibelle_actePs());
                    }
                    if (movement.getAdherant() != null) {
                        fraudDTO.setAdherentId(movement.getAdherant().getId());
                        fraudDTO.setAdherentName(movement.getAdherant().getName());
                    }

                    fraudulentMovements.add(fraudDTO);
                }
            }
        }

        return ResponseEntity.ok(fraudulentMovements);
    }

    @Data
    private static class FraudCheckResponse {
        private Long movementId;
        private boolean fraudulent;
        private String reason;
    }

}
