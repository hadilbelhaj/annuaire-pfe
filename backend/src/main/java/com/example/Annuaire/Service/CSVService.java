package com.example.Annuaire.Service;

import com.example.Annuaire.Exceptions.CSVProcessingException;
import com.example.Annuaire.Exceptions.InvalidCSVFormatException;
import com.example.Annuaire.Models.AdditionalAttributesPs;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Repository.AdditionalAttributesPsRepository;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CSVService {

    private static final List<String> REQUIRED_FIELDS = List.of(
            "nom",
            "prenom",
            "Specialite",
            "VilleAdresseCourrier",
            "Telephone",
            "TelephonePortable",
            "Email",
            "NumeroOrdre");

    private static final int BATCH_SIZE = 1000;
    @Autowired
    private final HealthcareProfessionalRepository healthcareProfessionalRepository;
    @Autowired
    private final ObjectMapper objectMapper;
    private final AdditionalAttributesPsRepository additionalAttributesPsRepository;

    public CSVService(HealthcareProfessionalRepository healthcareProfessionalRepository,
            ObjectMapper objectMapper, AdditionalAttributesPsRepository additionalAttributesPsRepository) {
        this.healthcareProfessionalRepository = healthcareProfessionalRepository;
        this.objectMapper = objectMapper;
        this.additionalAttributesPsRepository = additionalAttributesPsRepository;
    }

    @Async
    @Transactional
    public void importCSV(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            CSVParser parser = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
                    .parse(reader);

            validateHeaders(parser.getHeaderNames());

            List<HealthcareProfessional> batch = new ArrayList<>(BATCH_SIZE);
            List<Map<String, String>> additionalAttributesBatch = new ArrayList<>(BATCH_SIZE);
            long recordCount = 0;

            for (CSVRecord record : parser) {
                HealthcareProfessional professional = processRecord(record);
                Map<String, String> additionalFields = extractAdditionalFields(record);
                // show additional fields

                batch.add(professional);
                additionalAttributesBatch.add(additionalFields);
                recordCount++;

                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch, additionalAttributesBatch);
                    batch.clear();
                    additionalAttributesBatch.clear();
                }
            }

            if (!batch.isEmpty()) {
                saveBatch(batch, additionalAttributesBatch);
            }

            log.info("Successfully processed {} records from {}", recordCount, file.getOriginalFilename());

        } catch (Exception e) {
            log.error("CSV processing failed for file: {}", file.getOriginalFilename(), e);
            throw new CSVProcessingException("Échec du traitement du CSV", e);
        }
    }

    private HealthcareProfessional processRecord(CSVRecord record) {
        validateRequiredFields(record);
        HealthcareProfessional ps = new HealthcareProfessional(
                record.get("nom") + " " + record.get("prenom"),
                record.get("Specialite"),
                record.get("VilleAdresseCourrier"),
                record.get("Telephone"),
                record.get("TelephonePortable"),
                record.get("Email"),
                record.get("NumeroOrdre"),
                getRegionFromAddress(record.get("VilleAdresseCourrier")),
                null);
        return ps;
    }

    private Map<String, String> extractAdditionalFields(CSVRecord record) {
        return record.toMap().entrySet().stream()
                .filter(entry -> !REQUIRED_FIELDS.contains(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> normalizeHeader(entry.getKey()),
                        entry -> entry.getValue().trim(),
                        (existing, replacement) -> existing,
                        LinkedHashMap::new));
    }

    private void validateHeaders(List<String> headers) {
        List<String> missingHeaders = REQUIRED_FIELDS.stream()
                .filter(field -> !headers.contains(field))
                .collect(Collectors.toList());

        if (!missingHeaders.isEmpty()) {
            throw new InvalidCSVFormatException(
                    "headers obligatoires manquants : " + String.join(", ", missingHeaders));
        }
    }

    private void validateRequiredFields(CSVRecord record) {
        // only nom is required for now
        String value = record.get("nom");
        if (value == null || value.trim().isEmpty()) {
            throw new InvalidCSVFormatException(
                    "Champ obligatoire manquant à la ligne " + record.getRecordNumber() + ": nom");
        }
    }

    private String normalizeHeader(String header) {
        return header.toLowerCase().trim().replaceAll("\\s+", "_");
    }

    private void saveBatch(List<HealthcareProfessional> batch, List<Map<String, String>> additionalAttributesBatch) {
        try {
            healthcareProfessionalRepository.saveAll(batch);
            for (int i = 0; i < batch.size(); i++) {
                HealthcareProfessional professional = batch.get(i);
                Map<String, String> additionalFields = additionalAttributesBatch.get(i);

                // Save each additional attribute
                additionalFields.forEach((attributeName, attributeValue) -> {
                    AdditionalAttributesPs additionalAttributesPs = new AdditionalAttributesPs();
                    additionalAttributesPs.setAttributeName(attributeName);
                    additionalAttributesPs.setAttributeValue(attributeValue);
                    additionalAttributesPs.setHealthcareProfessional(professional);
                    additionalAttributesPsRepository.save(additionalAttributesPs);
                });
            }

        } catch (Exception e) {
            log.error("Batch save failed. Batch size: {}", batch.size(), e);
            throw new CSVProcessingException("Échec de la sauvegarde en base", e);
        }
    }

    private String getRegionFromAddress(String address) {
        List<String> tunisianRegions = Arrays.asList(
                "Ariana",
                "Beja",
                "Ben Arous",
                "Bizerte",
                "Gabes",
                "Gafsa",
                "Jendouba",
                "Kairouan",
                "Kasserine",
                "Kébili",
                "La Manouba",
                "Le Kef",
                "Mahdia",
                "Manouba",
                "Medenine",
                "Monastir",
                "Nabeul",
                "Sfax",
                "Sidi Bouzid",
                "Siliana",
                "Sousse",
                "Tataouine",
                "Tozeur",
                "Tunis",
                "Zaghouan");
        // check if address contains a region and return it
        if ("sans address".equals(address)) {
            return null;
        }
        return tunisianRegions.stream()
                .filter(region -> address.contains(region))
                .findFirst().orElse(null);
    }

}