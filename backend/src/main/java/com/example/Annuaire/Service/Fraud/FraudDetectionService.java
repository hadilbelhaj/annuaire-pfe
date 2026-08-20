package com.example.Annuaire.Service.Fraud;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.Annuaire.Models.MedicalClaim;

import org.springframework.http.*;
import java.util.Map;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Value;

@Service
public class FraudDetectionService {

    private String modelServiceUrl = "http://localhost:5000";

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean detectFraud(MedicalClaim claim) {
        try {

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", claim.getAmount());
            requestBody.put("specialtyAverageAmount", claim.getSpecialtyAverageAmount());
            requestBody.put("medicalSpecialty", claim.getMedicalSpecialty());
            requestBody.put("designation", claim.getDesignation());
            requestBody.put("reimbursementPercentage", claim.getReimbursementPercentage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    modelServiceUrl + "/detect-fraud",
                    requestEntity,
                    Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                return (Boolean) responseBody.get("isFraudulent");
            }

            return false;
        } catch (Exception e) {

            System.err.println("Error calling fraud detection model: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getDetailedAnalysis(MedicalClaim claim) {
        try {

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("amount", claim.getAmount());
            requestBody.put("specialtyAverageAmount", claim.getSpecialtyAverageAmount());
            requestBody.put("medicalSpecialty", claim.getMedicalSpecialty());
            requestBody.put("designation", claim.getDesignation());
            requestBody.put("reimbursementPercentage", claim.getReimbursementPercentage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    modelServiceUrl + "/detect-fraud",
                    requestEntity,
                    Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }

            return new HashMap<>();
        } catch (Exception e) {
            System.err.println("Error getting detailed analysis: " + e.getMessage());
            return new HashMap<>();
        }
    }
}
