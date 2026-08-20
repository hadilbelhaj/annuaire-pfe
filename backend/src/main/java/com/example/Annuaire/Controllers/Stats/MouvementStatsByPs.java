package com.example.Annuaire.Controllers.Stats;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Service.Statistiques.MouvementsParPsStats;
import com.example.DTOS.MouvementParPs.AdherentStatsDTO;
import com.example.DTOS.MouvementParPs.MonthlyStatsDTO;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.MouvementParPs.MovementStatisticsDTO;

@RestController
@RequestMapping("/api/healthcare-professionals")
public class MouvementStatsByPs {

    @Autowired
    private MouvementsParPsStats movementService;

    @GetMapping("/{id}/movements")
    public ResponseEntity<List<MovementDTO>> getMovementsForHealthcareProfessional(@PathVariable Long id) {
        List<MovementDTO> movements = movementService.getMovementsByHealthcareProfessionalId(id);
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{id}/movements/stats")
    public ResponseEntity<MovementStatisticsDTO> getMovementStatistics(@PathVariable Long id) {
        MovementStatisticsDTO stats = movementService.getStatisticsForHealthcareProfessional(id);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{id}/movements/monthly")
    public ResponseEntity<List<MonthlyStatsDTO>> getMonthlyStats(
            @PathVariable Long id,
            @RequestParam(required = false) Integer year) {

        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        List<MonthlyStatsDTO> monthlyStats = movementService.getMonthlyStatsForHealthcareProfessional(id, targetYear);
        return ResponseEntity.ok(monthlyStats);
    }

    @GetMapping("/{id}/movements/adherents")
    public ResponseEntity<List<AdherentStatsDTO>> getAdherentDistribution(@PathVariable Long id) {
        List<AdherentStatsDTO> adherentStats = movementService.getAdherentDistributionForHealthcareProfessional(id);
        return ResponseEntity.ok(adherentStats);
    }

}
