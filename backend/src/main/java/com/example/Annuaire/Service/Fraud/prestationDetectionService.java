package com.example.Annuaire.Service.Fraud;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.Prestation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.Data;


@Service
public class prestationDetectionService {

    @Autowired
    private RestTemplate restTemplate;
    private static final Logger log = LoggerFactory.getLogger(prestationDetectionService.class);

    @Value("${prediction.service.url}")
    private String predictionServiceUrl;

    public boolean isFraudulentMovement(Movement movement) {
        HealthcareProfessional professional = movement.getHealthcareProfessional();
        String specialty = professional.getmedicalSpecialty();

        Prestation prestation = movement.getActePS().getPrestation();
        String prestationLibelle = prestation.getPrestation_libelle().toString();

        List<String> allowedPrestations = getAllowedPrestations(specialty);

        return !allowedPrestations.contains(prestationLibelle);
    }

    private List<String> getAllowedPrestations(String specialty) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(predictionServiceUrl)
                    .queryParam("specialty", specialty);

            // Make the request to prediction service
            ResponseEntity<PredictionResponse> response = restTemplate.getForEntity(
                    builder.toUriString(),
                    PredictionResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getSuggestions();
            } else {
                // Log error and return empty list as fallback
                log.error("Failed to get predictions for specialty: {}", specialty);
                return Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Error calling prediction service", e);
            return Collections.emptyList();
        }
    }

    @Data
    private static class PredictionResponse {
        private String specialty;
        private List<String> suggestions;
    }
}
