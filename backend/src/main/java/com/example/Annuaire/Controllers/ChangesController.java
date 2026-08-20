package com.example.Annuaire.Controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Service.ChangesService;
import com.example.Annuaire.Service.ChangesSubServices.AuditChangesService;
import com.example.Annuaire.Service.ChangesSubServices.BackupService;

@RestController
@RequestMapping("/api/changes")
public class ChangesController {
    private final ChangesService changeService;
    private final BackupService backupService;
    private final AuditChangesService auditService;

    public ChangesController(ChangesService changeService, BackupService backupService,
            AuditChangesService auditService) {
        this.changeService = changeService;
        this.backupService = backupService;
        this.auditService = auditService;
    }

    @PostMapping("/apply/{date}")
    public ResponseEntity<String> applyChanges(@PathVariable String date) throws IOException {
        backupService.createBackup();
        changeService.applyChanges(date);
        return ResponseEntity.ok("Changes applied successfully");
    }

    @PostMapping("/revert")
    public ResponseEntity<String> revertChanges(@RequestParam String date) throws IOException {
        backupService.restoreBackup(date);
        return ResponseEntity.ok("Database reverted from backup");
    }

}
