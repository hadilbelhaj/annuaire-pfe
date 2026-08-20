package com.example.Annuaire.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.Annuaire.Models.Remboursement;
import com.example.Annuaire.Service.RemboursementService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/remboursements")
public class RemboursementController {

    private final RemboursementService remboursementService;

    @Autowired
    public RemboursementController(RemboursementService remboursementService) {
        this.remboursementService = remboursementService;
    }

    @PostMapping("/import-file")
    public ResponseEntity<String> importRemboursementsFromFile(@RequestParam("file") MultipartFile file) {
        try {
            // Create a temporary file to store the uploaded content
            Path tempFile = Files.createTempFile("remboursements", ".json");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Process the file
            List<Remboursement> importedRemboursements = remboursementService
                    .importRemboursementsFromJson(tempFile.toString());

            // Clean up
            Files.deleteIfExists(tempFile);

            return ResponseEntity.ok("Successfully imported " + importedRemboursements.size() + " remboursements");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to import remboursements: " + e.getMessage());
        }
    }

    @PostMapping("/import-json")
    public ResponseEntity<String> importRemboursementsFromJson(@RequestBody String jsonData) {
        try {
            List<Remboursement> importedRemboursements = remboursementService
                    .importRemboursementsFromJsonString(jsonData);

            return ResponseEntity.ok("Successfully imported " + importedRemboursements.size() + " remboursements");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to import remboursements: " + e.getMessage());
        }
    }
}