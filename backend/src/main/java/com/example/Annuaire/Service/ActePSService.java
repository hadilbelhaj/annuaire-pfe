package com.example.Annuaire.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.ActePS;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Prestation;
import com.example.Annuaire.Repository.ActePsRepository;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.PrestationRepository;
import com.example.Annuaire.enums.Enums;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class ActePSService {

    private final HealthcareProfessionalRepository hpRepository;
    private final PrestationRepository prestationRepository;
    private final ActePsRepository actePSRepository;

    @Autowired
    public ActePSService(
            HealthcareProfessionalRepository hpRepository,
            PrestationRepository prestationRepository,
            ActePsRepository actePSRepository) {
        this.hpRepository = hpRepository;
        this.prestationRepository = prestationRepository;
        this.actePSRepository = actePSRepository;
    }

    public List<ActePS> getPrestationsByHealthcareProfessionalId(Long healthcareProfessionalId) {
        return actePSRepository.findByHealthcareProfessionalId(healthcareProfessionalId);
    }

    @Transactional
    public List<ActePS> associatePrestationsToPS(Long healthcareProfessionalId, List<String> prestationLabels) {
        HealthcareProfessional hp = hpRepository.findById(healthcareProfessionalId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Healthcare Professional not found with id: " + healthcareProfessionalId));

        String medicalSpecialty = hp.getmedicalSpecialty();
        List<ActePS> existingActes = actePSRepository.findByHealthcareProfessionalId(healthcareProfessionalId);
        Set<String> existingLabels = existingActes.stream()
                .map(ActePS::getLibelle_actePs)
                .collect(Collectors.toSet());

        List<ActePS> createdActes = new ArrayList<>();

        for (String prestationLabel : prestationLabels) {
            Enums.Prestation_libelle prestationEnum = null;
            try {
                prestationEnum = Enums.Prestation_libelle.valueOf(prestationLabel);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid prestation label: " + prestationLabel);
            }

            // Find the prestation by its enum label
            Prestation prestation = prestationRepository.findByPrestationLibelle(prestationEnum)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Prestation not found with label: " + prestationLabel));

            // Format the ActePS label as "PrestationName-MedicalSpecialty"
            String formattedLabel = prestationLabel + "-" + medicalSpecialty;

            // Skip if this formatted prestation is already associated
            if (existingLabels.contains(formattedLabel)) {
                continue;
            }

            // Create and save the ActePS
            ActePS actePS = new ActePS();
            actePS.setLibelle_actePs(formattedLabel);
            actePS.setPrestation(prestation);
            actePS.setHealthcareProfessional(hp);

            actePS = actePSRepository.save(actePS);
            createdActes.add(actePS);
        }

        return createdActes;
    }
}
