package com.example.Annuaire.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.Annuaire.Service.CSVService;

@RestController
@RequestMapping("/api/upload")
public class UploadCsvController {
    @Autowired
    private final CSVService csvService;

    public UploadCsvController(CSVService csvService) {
        this.csvService = csvService;
    }

    @PostMapping()
    public ResponseEntity<String> uploadCSV(@RequestParam("file") MultipartFile file) {
        try {
            csvService.importCSV(file);
            return ResponseEntity.ok("Succès de l'import CSV");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
