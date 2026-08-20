package com.example.Annuaire.Controllers;

import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Service.PSMovementsService;
import com.example.DTOS.MouvementParPs.MovementDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ps")
public class PSMovementsController {

    private final PSMovementsService psMovementsService;

    @Autowired
    public PSMovementsController(PSMovementsService psMovementsService) {
        this.psMovementsService = psMovementsService;
    }

    @GetMapping("/movements/name/{name}")
    public ResponseEntity<List<MovementDTO>> getMovementDTOsByPSName(@PathVariable String name) {
        List<MovementDTO> movementDTOs = psMovementsService.getMovementDTOsByPSName(name);
        return ResponseEntity.ok(movementDTOs);
    }

    @GetMapping("/movements/id/{id}")
    public ResponseEntity<List<MovementDTO>> getMovementDTOsByPSId(@PathVariable Long id) {
        List<MovementDTO> movementDTOs = psMovementsService.getMovementDTOsByPSId(id);
        return ResponseEntity.ok(movementDTOs);
    }
}
