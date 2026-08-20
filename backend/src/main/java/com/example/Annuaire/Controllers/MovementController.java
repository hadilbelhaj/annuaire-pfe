package com.example.Annuaire.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.ActePsRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Service.MovementService;
import com.example.Annuaire.enums.Enums.Prestation_libelle;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.MovementsTable.GroupingByAdherantDto;
import com.example.DTOS.MovementsTable.GroupingByDateDto;
import com.example.DTOS.MovementsTable.GroupingByPsDto;
import com.example.DTOS.mouvement.MovementResponseDTO;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/movements")
public class MovementController {
    @Autowired
    private final MovementRepository movementsRepository;
    @Autowired
    private final MovementService movementsService;

    private final ActePsRepository actePsRepository;

    public MovementController(MovementRepository movementsRepository,
            MovementService movementsService, ActePsRepository actePsRepository) {
        this.movementsRepository = movementsRepository;
        this.movementsService = movementsService;
        this.actePsRepository = actePsRepository;
    }

    @GetMapping
    public List<Movement> getMovements() {
        return movementsRepository.findAll();
    }

    @GetMapping("/all")
    public ResponseEntity<List<MovementResponseDTO>> getAllMovements() {
        List<MovementResponseDTO> movements = movementsService.getAllMovementsWithDetails();
        return ResponseEntity.ok(movements);
    }

    @GetMapping("/{page}/{size}")
    public Page<Movement> getMovements(@PathVariable int page, @PathVariable int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        return movementsRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/groupbyps/{page}/{size}")
    public Page<GroupingByPsDto> getMovementsGroupByPs(@PathVariable int page,
            @PathVariable int size, @RequestParam String search, @RequestParam String sort) {
        return movementsService.findMovementsGroupByHealthCareProfessional(search, sort, page,
                size);
    }

    @GetMapping("/groupbyadherant/{page}/{size}")
    public Page<GroupingByAdherantDto> getMovementsGroupByAdherant(@PathVariable int page,
            @PathVariable int size, @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        return movementsService.findMovmentsGroupByAdherant(search, sort, page, size);
    }

    @GetMapping("/groupbydate/{page}/{size}")
    public Page<GroupingByDateDto> getMovementsGroupByDate(@PathVariable int page,
            @PathVariable int size, @RequestParam String period,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        return movementsService.findMovemntGroupByDate(period, search, sort, page, size);
    }

    @GetMapping("/stats/distribution")
    public ResponseEntity<List<Map<String, Object>>> getAmountDistribution() {
        return ResponseEntity.ok(movementsRepository.getAmountDistribution());
    }

    @GetMapping("/stats/trend")
    public ResponseEntity<List<Map<String, Object>>> getMonthlyTrend() {
        return ResponseEntity.ok(movementsRepository.getMonthlyTrend());
    }

    @GetMapping("/stats/top-professionals")
    public ResponseEntity<List<Map<String, Object>>> getTopProfessionals(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(movementsRepository.getTopProfessionals(limit));
    }

    @Modifying
    @Transactional
    @PostMapping("/setactpslib")
    public ResponseEntity<?> setactpslib() {

        List<Movement> ms = movementsRepository.findAll();
        String s = "";
        for (Movement m : ms) {
            Prestation_libelle prestation = m.getActePS().getPrestation().getPrestation_libelle();
            String specialite = m.getActePS().getHealthcareProfessional().getmedicalSpecialty();
            s = s.concat(prestation + "-" + specialite + "\n");
            actePsRepository.updatelibelle(prestation + "-" + specialite, m.getActePS().getId());

        }
        return ResponseEntity.ok(s);
    }

}
