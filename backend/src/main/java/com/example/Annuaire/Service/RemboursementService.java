package com.example.Annuaire.Service;

import com.example.Annuaire.Models.Adherant;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.Remboursement;
import com.example.Annuaire.Repository.AdherantRepository;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Repository.RemboursementRepository;
import com.example.DTOS.RemboursementDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final MovementRepository movementRepository;
    private final AdherantRepository adherantRepository;
    private final HealthcareProfessionalRepository healthcareProfessionalRepository;

    @Autowired
    public RemboursementService(RemboursementRepository remboursementRepository,
            MovementRepository movementRepository,
            AdherantRepository adherantRepository,
            HealthcareProfessionalRepository healthcareProfessionalRepository) {
        this.remboursementRepository = remboursementRepository;
        this.movementRepository = movementRepository;
        this.adherantRepository = adherantRepository;
        this.healthcareProfessionalRepository = healthcareProfessionalRepository;
    }

    @Transactional
    public List<Remboursement> importRemboursementsFromJson(String jsonFilePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // For handling LocalDateTime

        // Read JSON file into a list of DTOs
        List<RemboursementDTO> dtoList = objectMapper.readValue(
                new File(jsonFilePath),
                new TypeReference<List<RemboursementDTO>>() {
                });

        List<Remboursement> savedRemboursements = new ArrayList<>();

        for (RemboursementDTO dto : dtoList) {
            Remboursement remboursement = new Remboursement();

            // Find related Movement
            Optional<Movement> movementOpt = movementRepository.findById(dto.getMovementId());
            if (movementOpt.isPresent()) {
                remboursement.setMovement(movementOpt.get());
            } else {
                // You might want to handle this case differently based on your requirements
                System.out.println("Movement not found for ID: " + dto.getMovementId());
                continue;
            }

            // Find related Adherant
            Optional<Adherant> adherantOpt = adherantRepository.findById(dto.getAdherantId());
            if (adherantOpt.isPresent()) {
                remboursement.setAdherant(adherantOpt.get());
            } else {
                System.out.println("Adherant not found for ID: " + dto.getAdherantId());
                continue;
            }

            // Set other properties from DTO
            remboursement.setAmount(dto.getAmount());
            remboursement.setSpecialtyAverageAmount(dto.getSpecialtyAverageAmount());
            remboursement.setReimbursementPercentage(dto.getReimbursementPercentage());
            remboursement.setInsuranceAmount(dto.getInsuranceAmount());
            remboursement.setAdherantAmount(dto.getAdherantAmount());
            remboursement.setDate(dto.getDate());

            // Save the remboursement
            Remboursement savedRemboursement = remboursementRepository.save(remboursement);
            savedRemboursements.add(savedRemboursement);
        }

        return savedRemboursements;
    }

    // Alternative method if you already have the JSON as a string
    @Transactional
    public List<Remboursement> importRemboursementsFromJsonString(String jsonString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        List<RemboursementDTO> dtoList = objectMapper.readValue(
                jsonString,
                new TypeReference<List<RemboursementDTO>>() {
                });

        List<Remboursement> savedRemboursements = new ArrayList<>();

        for (RemboursementDTO dto : dtoList) {
            // Same conversion logic as above
            Remboursement remboursement = convertDtoToEntity(dto);
            if (remboursement != null) {
                Remboursement savedRemboursement = remboursementRepository.save(remboursement);
                savedRemboursements.add(savedRemboursement);
            }
        }

        return savedRemboursements;
    }

    private Remboursement convertDtoToEntity(RemboursementDTO dto) {
        // Find related entities
        Optional<Movement> movementOpt = movementRepository.findById(dto.getMovementId());
        Optional<Adherant> adherantOpt = adherantRepository.findById(dto.getAdherantId());

        if (!movementOpt.isPresent() || !adherantOpt.isPresent()) {
            // Required related entities not found
            return null;
        }

        Remboursement remboursement = new Remboursement();
        remboursement.setMovement(movementOpt.get());
        remboursement.setAdherant(adherantOpt.get());
        remboursement.setAmount(dto.getAmount());
        remboursement.setSpecialtyAverageAmount(dto.getSpecialtyAverageAmount());
        remboursement.setReimbursementPercentage(dto.getReimbursementPercentage());
        remboursement.setInsuranceAmount(dto.getInsuranceAmount());
        remboursement.setAdherantAmount(dto.getAdherantAmount());
        remboursement.setDate(dto.getDate());

        return remboursement;
    }
}
