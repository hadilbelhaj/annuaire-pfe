package com.example.Annuaire.Controllers;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.Annuaire.Service.SyncService;

import org.slf4j.Logger;

@RestController
@RequestMapping("/api/sync")
@CrossOrigin(origins = "*")

public class SyncController {
    private static final Logger logger = LoggerFactory.getLogger(SyncController.class);
    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    @PostMapping
    public ResponseEntity<String> triggerSync() {
        logger.info("Received request to trigger synchronization.");
        try {
            String result = syncService.performFullSync();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("Synchronization failed: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body("Synchronization failed. Check logs for details." + e.getMessage() + e);
        }
    }

}
