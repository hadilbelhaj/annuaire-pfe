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

@RestController
@RequestMapping("/api/stats")
public class HealthcareProfessionalStatsController {
    private final StatsRetrievalService statsRetrievalService;

    @Autowired
    public HealthcareProfessionalStatsController(StatsRetrievalService statsRetrievalService) {
        this.statsRetrievalService = statsRetrievalService;
    }

    @GetMapping("/professionals")
    public ResponseEntity<HealthcareProfessionalStatsDTO> getProfessionalStats() {
        HealthcareProfessionalStatsDTO stats = statsRetrievalService.getLatestStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/professionals/specialty")
    public ResponseEntity<List<SpecialtyDistributionDTO>> getSpecialtyDistribution() {
        HealthcareProfessionalStatsDTO stats = statsRetrievalService.getLatestStats();
        return ResponseEntity.ok(stats.getSpecialtyDistribution());
    }

    @GetMapping("/professionals/region")
    public ResponseEntity<List<RegionDistributionDTO>> getRegionDistribution() {
        HealthcareProfessionalStatsDTO stats = statsRetrievalService.getLatestStats();
        return ResponseEntity.ok(stats.getRegionDistribution());
    }

    @GetMapping("/professionals/top")
    public ResponseEntity<Map<String, List<TopProfessionalDTO>>> getTopProfessionals() {
        HealthcareProfessionalStatsDTO stats = statsRetrievalService.getLatestStats();
        
        Map<String, List<TopProfessionalDTO>> topProfessionals = new HashMap<>();
        topProfessionals.put("topByVisits", stats.getTopByVisits());
        topProfessionals.put("topByTransactions", stats.getTopByTransactionVolume());
        topProfessionals.put("topByAverage", stats.getTopByAverageAmount());
        
        return ResponseEntity.ok(topProfessionals);
    }
}