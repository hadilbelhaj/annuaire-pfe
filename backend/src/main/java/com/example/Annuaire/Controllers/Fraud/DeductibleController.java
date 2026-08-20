package com.example.Annuaire.Controllers.Fraud;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Annuaire.Service.Fraud.DeductibleTrackingService;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deductible")
public class DeductibleController {

    private final DeductibleTrackingService deductibleTrackingService;

    @Autowired
    public DeductibleController(DeductibleTrackingService deductibleTrackingService) {
        this.deductibleTrackingService = deductibleTrackingService;
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkDeductibleStatus(
            @RequestParam(value = "adherentName") String adherentName) {
        try {

            Map<String, Object> result = deductibleTrackingService.checkDeductibleStatus(adherentName);

            if (result.containsKey("error")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {

            Map<String, Object> error = Map.of("error", "Error processing request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @GetMapping("/approaching")
    public ResponseEntity<List<Map<String, Object>>> findAdherentsApproachingDeductible(
            @RequestParam(defaultValue = "100.0") double threshold) {

        List<Map<String, Object>> adherents = deductibleTrackingService.findAdherentsApproachingDeductible(threshold);
        return ResponseEntity.ok(adherents);
    }

    @GetMapping("/exceeded")
    public ResponseEntity<List<Map<String, Object>>> findAdherentsExceedingDeductible(
            @RequestParam(defaultValue = "2025") int year) {
        List<Map<String, Object>> adherentsExceedingDeductible = deductibleTrackingService
                .findAdherentsExceedingDeductibleWithClaimDetails(year);
        return ResponseEntity.ok(adherentsExceedingDeductible);
    }
}
