package com.example.Annuaire.Controllers.Fraud;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.FraudAlert;
import com.example.Annuaire.Models.Remboursement;
import com.example.Annuaire.Repository.RemboursementRepository;
import com.example.Annuaire.Service.Fraud.FormuleFraudDetection;
import com.example.Annuaire.Service.Fraud.HistoriquePrestationFraud;

@RestController
@RequestMapping("/api/formule/fraud")
public class FormuleFraudController {
    @Autowired
    private RemboursementRepository remboursementRepository;

    @Autowired
    private HistoriquePrestationFraud historiquePrestationFraud;

    @Autowired
    private FormuleFraudDetection formuleFraudDetectionService;

    @GetMapping("/check/{remboursementId}")
    public ResponseEntity<List<FraudAlert>> checkForFraud(@PathVariable Long remboursementId) {
        // Find the reimbursement
        Remboursement remboursement = remboursementRepository.findById(remboursementId)
                .orElseThrow(() -> new RuntimeException("Reimbursement not found"));

        // Check for both types of fraud
        List<FraudAlert> alerts = historiquePrestationFraud.detectFraud(remboursement);
        alerts.addAll(formuleFraudDetectionService.detectFormuleMismatch(remboursement));

        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/check-all")
    public ResponseEntity<List<FraudAlert>> checkAllForFraud() {
        List<Remboursement> allRemboursements = remboursementRepository.findAll();
        List<FraudAlert> allAlerts = new ArrayList<>();

        for (Remboursement remboursement : allRemboursements) {
            List<FraudAlert> alerts = formuleFraudDetectionService.detectFormuleMismatch(remboursement);
            allAlerts.addAll(alerts);
        }

        return ResponseEntity.ok(allAlerts);
    }

}
