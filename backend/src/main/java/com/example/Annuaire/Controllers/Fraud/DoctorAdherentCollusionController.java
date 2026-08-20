package com.example.Annuaire.Controllers.Fraud;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.CollusionFlag;
import com.example.Annuaire.Models.CollusionThresholds;
import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Service.HealthcareProfessionalService;
import com.example.Annuaire.Service.Fraud.DoctorAdherentCollusionDetector;

@RestController
@RequestMapping("/api/fraud")
public class DoctorAdherentCollusionController {
    @Autowired
    private DoctorAdherentCollusionDetector collusionDetector;

    @Autowired
    private MovementRepository movementRepository;
    @Autowired
    private HealthcareProfessionalService lookupService;

    @GetMapping("/collusion/detect")
    public ResponseEntity<List<CollusionFlag>> detectCollusion() {
        CollusionThresholds thresholds = new CollusionThresholds();

        List<Movement> movements = movementRepository.findAll();

        List<CollusionFlag> flags = collusionDetector.detectCollusion(movements, thresholds);

        return ResponseEntity.ok(flags);
    }

    @PostMapping("/collusion/detect-custom")
    public ResponseEntity<List<CollusionFlag>> detectCollusionWithCustomThresholds(
            @RequestBody CollusionThresholds thresholds) {

        List<Movement> movements = movementRepository.findAll();

        List<CollusionFlag> flags = collusionDetector.detectCollusion(movements, thresholds);

        return ResponseEntity.ok(flags);
    }

    @GetMapping("/collusion/detect-by-date")
    public ResponseEntity<List<CollusionFlag>> detectCollusionByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
        LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");

        // Use default thresholds
        CollusionThresholds thresholds = new CollusionThresholds();

        // Get movements for the specified date range
        List<Movement> movements = movementRepository.findByDateBetween(start, end);

        // Detect collusion
        List<CollusionFlag> flags = collusionDetector.detectCollusion(movements, thresholds);

        return ResponseEntity.ok(flags);
    }

    @GetMapping("/collusion/detect-by-professional")
    public ResponseEntity<List<CollusionFlag>> detectCollusionByProfessional(
            @RequestParam Long professionalId) {

        CollusionThresholds thresholds = new CollusionThresholds();

        List<Movement> movements = movementRepository.findByHealthcareProfessionalId(professionalId);

        List<CollusionFlag> flags = collusionDetector.detectCollusion(movements, thresholds);

        return ResponseEntity.ok(flags);
    }

    @GetMapping("/collusion/detect-by-adherent")
    public ResponseEntity<List<CollusionFlag>> detectCollusionByAdherent(
            @RequestParam Long adherentId) {

        // Use default thresholds
        CollusionThresholds thresholds = new CollusionThresholds();

        // Get movements for the specified adherent
        List<Movement> movements = movementRepository.findByAdherantId(adherentId);

        // Detect collusion
        List<CollusionFlag> flags = collusionDetector.detectCollusion(movements, thresholds);

        return ResponseEntity.ok(flags);
    }

    @GetMapping("/collusion/detect-by-professional-name")
    public ResponseEntity<List<CollusionFlag>> detectCollusionByProfessionalName(
            @RequestParam String professionalName) {

        Long professionalId = lookupService.findHealthcareProfessionalIdByName(professionalName);
        return detectCollusionByProfessional(professionalId);
    }

    @GetMapping("/collusion/detect-by-adherent-name")
    public ResponseEntity<List<CollusionFlag>> detectCollusionByAdherentName(
            @RequestParam String firstName, @RequestParam String lastName) {

        Long adherentId = lookupService.findAdherentIdByName(firstName, lastName);
        return detectCollusionByAdherent(adherentId);
    }

}
