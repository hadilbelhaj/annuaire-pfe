package com.example.Annuaire.Controllers.Fraud;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.FraudAlert;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.Remboursement;
import com.example.Annuaire.Repository.RemboursementRepository;
import com.example.Annuaire.Service.Fraud.FormuleFraudDetection;
import com.example.Annuaire.Service.Fraud.HistoriquePrestationFraud;

@RestController
@RequestMapping("/api/fraud")
public class HistoriqueFraudController {
        @Autowired
        private HistoriquePrestationFraud historiquePrestationFraud;
        @Autowired
        private FormuleFraudDetection formuleFraudDetectionService;

        @Autowired
        private RemboursementRepository remboursementRepository;

        record EnhancedFraudCheckResult(
                        Long remboursementId,
                        String adherantName,
                        String doctorName,
                        LocalDateTime movementDate,
                        String movementDescription,
                        BigDecimal amount,
                        List<FraudAlert> alerts) {
        }

        private EnhancedFraudCheckResult toEnhancedResult(Remboursement r) {
                Movement movement = r.getMovement();
                String adherantName = r.getAdherant().getFirstName() + " " + r.getAdherant().getLastName();
                String doctorName = movement.getActePS().getHealthcareProfessional().getName();

                // Get fraud alerts from both services
                List<FraudAlert> historicalAlerts = historiquePrestationFraud.detectFraud(r);

                List<FraudAlert> formuleAlerts = formuleFraudDetectionService.detectFormuleMismatch(r);
                System.out.println("Formule alerts found: " + formuleAlerts.size());

                // Combine all alerts
                List<FraudAlert> allAlerts = new ArrayList<>(historicalAlerts);
                allAlerts.addAll(formuleAlerts);

                return new EnhancedFraudCheckResult(
                                r.getId(),
                                adherantName,
                                doctorName,
                                movement.getDate(),
                                movement.getDescription(),
                                r.getAmount(),
                                allAlerts);
        }

        @GetMapping("/check/{remboursementId}")
        public ResponseEntity<?> checkRemboursementForFraud(@PathVariable Long remboursementId) {
                Optional<Remboursement> remboursementOpt = remboursementRepository.findById(remboursementId);

                if (!remboursementOpt.isPresent()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body("Remboursement with ID " + remboursementId + " not found");
                }

                Remboursement remboursement = remboursementOpt.get();
                EnhancedFraudCheckResult result = toEnhancedResult(remboursement);

                return ResponseEntity.ok(result);
        }

        @PostMapping("/check-batch")
        public ResponseEntity<?> checkBatchForFraud(@RequestBody List<Long> remboursementIds) {
                if (remboursementIds == null || remboursementIds.isEmpty()) {
                        return ResponseEntity.badRequest().body("No reimbursement IDs provided");
                }

                List<Remboursement> remboursements = remboursementRepository.findAllById(remboursementIds);

                if (remboursements.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body("None of the provided reimbursement IDs were found");
                }

                List<EnhancedFraudCheckResult> results = remboursements.stream()
                                .map(this::toEnhancedResult)
                                .toList();

                return ResponseEntity.ok(results);
        }

        @GetMapping("/check-date-range")
        public ResponseEntity<?> checkDateRangeForFraud(
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

                if (startDate.isAfter(endDate)) {
                        return ResponseEntity.badRequest().body("Start date must be before end date");
                }
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);

                List<Remboursement> remboursements = remboursementRepository
                                .findByMovementDateBetween(startDateTime, endDateTime);

                if (remboursements.isEmpty()) {
                        return ResponseEntity.ok("No reimbursements found in the specified date range");
                }

                List<EnhancedFraudCheckResult> results = remboursements.stream()
                                .map(this::toEnhancedResult)
                                .filter(result -> !result.alerts().isEmpty())
                                .toList();

                return ResponseEntity.ok(results);
        }

        @GetMapping("/stats")
        public ResponseEntity<?> getFraudStats() {
                List<Remboursement> allReimbursements = remboursementRepository.findAll();

                record FraudStats(
                                long totalReimbursements,
                                long totalFraudulentReimbursements,
                                double fraudPercentage,
                                Map<String, Long> fraudByDoctor,
                                Map<String, Long> fraudByAdherant) {
                }
                List<Remboursement> fraudulentReimbursements = allReimbursements.stream()
                                .filter(r -> !historiquePrestationFraud.detectFraud(r).isEmpty())
                                .toList();

                long totalFraudulent = fraudulentReimbursements.size();
                double fraudPercentage = allReimbursements.isEmpty() ? 0
                                : (double) totalFraudulent / allReimbursements.size() * 100;

                Map<String, Long> fraudByDoctor = fraudulentReimbursements.stream()
                                .collect(Collectors.groupingBy(
                                                r -> r.getMovement().getHealthcareProfessional().getName(),
                                                Collectors.counting()));

                Map<String, Long> fraudByAdherant = fraudulentReimbursements.stream()
                                .collect(Collectors.groupingBy(
                                                r -> r.getAdherant().getFirstName() + " "
                                                                + r.getAdherant().getLastName(),
                                                Collectors.counting()));

                FraudStats stats = new FraudStats(
                                allReimbursements.size(),
                                totalFraudulent,
                                fraudPercentage,
                                fraudByDoctor,
                                fraudByAdherant);

                return ResponseEntity.ok(stats);
        }

        @GetMapping("/check-all")
        public ResponseEntity<?> checkAllReimbursements(
                        @RequestParam(defaultValue = "false") boolean onlyFraudulent) {

                List<Remboursement> allReimbursements = remboursementRepository.findAll();

                if (allReimbursements.isEmpty()) {
                        return ResponseEntity.ok("No reimbursements found in the system");
                }

                List<EnhancedFraudCheckResult> results = allReimbursements.stream()
                                .map(this::toEnhancedResult)
                                .filter(result -> !onlyFraudulent || !result.alerts().isEmpty())
                                .toList();

                return ResponseEntity.ok(results);
        }
}