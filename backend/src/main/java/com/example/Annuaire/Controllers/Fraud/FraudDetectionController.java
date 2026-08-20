package com.example.Annuaire.Controllers.Fraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.MedicalClaim;
import com.example.Annuaire.Service.Fraud.FraudDetectionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/claims")
public class FraudDetectionController {

    private final FraudDetectionService fraudDetectionService;

    @Autowired
    public FraudDetectionController(FraudDetectionService fraudDetectionService) {
        this.fraudDetectionService = fraudDetectionService;
    }

    @PostMapping("/check-fraud")
    public ResponseEntity<Map<String, Object>> checkFraud(@RequestBody MedicalClaim claim) {
        boolean isFraudulent = fraudDetectionService.detectFraud(claim);

        Map<String, Object> response = new HashMap<>();
        response.put("isFraudulent", isFraudulent);
        response.put("claim", claim);

        return ResponseEntity.ok(response);
    }

    
    @PostMapping("/analyze")
    public ResponseEntity<Map<String, Object>> analyzeClaimDetails(@RequestBody MedicalClaim claim) {
        Map<String, Object> analysisResult = fraudDetectionService.getDetailedAnalysis(claim);
        return ResponseEntity.ok(analysisResult);
    }
}
