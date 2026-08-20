package com.example.Annuaire.Controllers.Stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.Annuaire.Service.Statistiques.PrestationStatisticsService;
import com.example.DTOS.ps.HealthcareProfessionalStatsDTO;
import com.example.DTOS.ps.PrestationBySpecialtyDTO;
import com.example.DTOS.ps.PrestationMonthlyStatsDTO;
import com.example.DTOS.ps.PrestationStatsDTO;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
public class PrestationStatisticsController {

    @Autowired
    private PrestationStatisticsService service;

    @GetMapping("/prestations/most-frequent")
    public ResponseEntity<List<PrestationStatsDTO>> getMostFrequentPrestations(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getMostFrequentPrestations(limit));
    }

    @GetMapping("/prestations/top-revenue")
    public ResponseEntity<List<PrestationStatsDTO>> getTopRevenuePrestations(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(service.getTopRevenuePrestations(limit));
    }

    @GetMapping("/prestations/{prestationId}/monthly")
    public ResponseEntity<List<PrestationMonthlyStatsDTO>> getMonthlyPrestationStats(
            @PathVariable Long prestationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return ResponseEntity.ok(
                service.getMonthlyPrestationStats(prestationId, startDate, endDate));
    }

    @GetMapping("/healthcare-professionals")
    public ResponseEntity<Page<HealthcareProfessionalStatsDTO>> getHealthcareProfessionalStats(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                service.getHealthcareProfessionalStats(
                        specialty, region, PageRequest.of(page, size)));
    }

    @GetMapping("/specialties/{specialty}/top-prestations")
    public ResponseEntity<List<PrestationBySpecialtyDTO>> getTopPrestationsBySpecialty(
            @PathVariable String specialty,
            @RequestParam(defaultValue = "10") int limit) {

        return ResponseEntity.ok(
                service.getTopPrestationsBySpecialty(specialty, limit));
    }
}
