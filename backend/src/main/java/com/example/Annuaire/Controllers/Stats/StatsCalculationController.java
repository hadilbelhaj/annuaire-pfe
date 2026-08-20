package com.example.Annuaire.Controllers.Stats;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Service.Statistiques.HealthcareProfessionalStatsService;
import com.example.DTOS.ps.HealthcareProfessionalStatsDTO;
import com.example.DTOS.ps.RegionDistributionDTO;
import com.example.DTOS.ps.SpecialtyDistributionDTO;
import com.example.DTOS.ps.TopProfessionalDTO;
import com.example.Annuaire.Service.Statistiques.StatsRetrievalService;
import org.springframework.http.HttpStatus;
import java.util.Optional;
import com.example.Annuaire.Models.PrecalculatedProfessionalStats;
import com.example.Annuaire.Repository.PrecalculatedStatsRepository;

@RestController
@RequestMapping("/api/stats/calculation")
public class StatsCalculationController {
    private final HealthcareProfessionalStatsService statsCalculationService;

    @Autowired
    public StatsCalculationController(HealthcareProfessionalStatsService statsCalculationService) {
        this.statsCalculationService = statsCalculationService;
    }
    @Autowired
    private PrecalculatedStatsRepository precalculatedStatsRepository;


    @PostMapping("/trigger")
    public ResponseEntity<String> triggerStatsCalculation() {
        try {
            statsCalculationService.calculateAndPersistStats();
            return ResponseEntity.ok("Stats calculation completed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Failed to calculate stats: " + e.getMessage());
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getCalculationStatus() {
        Optional<PrecalculatedProfessionalStats> lastCalculation = 
            precalculatedStatsRepository.findFirstByOrderByCalculatedAtDesc();
        
        Map<String, Object> status = new HashMap<>();
        
        if (lastCalculation.isPresent()) {
            status.put("lastCalculatedAt", lastCalculation.get().getCalculatedAt());
            status.put("status", "Last calculation completed successfully");
        } else {
            status.put("status", "No stats calculations have been performed yet");
        }
        
        return ResponseEntity.ok(status);
    }
}