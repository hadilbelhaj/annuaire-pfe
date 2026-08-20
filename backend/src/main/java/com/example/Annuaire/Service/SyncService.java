package com.example.Annuaire.Service;

import org.springframework.stereotype.Service;

import com.example.Annuaire.Service.SyncSubServices.DataFileService;
import com.example.Annuaire.Service.SyncSubServices.NodeScriptRunner;
import com.example.Annuaire.Service.SyncSubServices.PythonServiceCleaner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SyncService {

    private static final Logger logger = LoggerFactory.getLogger(SyncService.class);
    private final NodeScriptRunner nodeScriptRunner;
    private final PythonServiceCleaner pythonServiceCleaner;
    private final DataFileService dataFileService;

    public SyncService(NodeScriptRunner nodeScriptRunner, PythonServiceCleaner pythonServiceCleaner,
            DataFileService dataFileService) {
        this.nodeScriptRunner = nodeScriptRunner;
        this.pythonServiceCleaner = pythonServiceCleaner;
        this.dataFileService = dataFileService;
    }

    public String performFullSync() {
        try {
            logger.info("Starting full synchronization...");
            String resultNode = nodeScriptRunner.runNodeScript();
            String resultPython = pythonServiceCleaner.runPythonCleaner(
                    dataFileService.getTodayJsonFile().getAbsolutePath());
            logger.info("Synchronization completed successfully.");
            return resultNode + resultPython;
        } catch (Exception e) {
            logger.error("Synchronization failed: {}", e.getMessage(), e);
            return "Synchronization failed. Check logs for details.";
        }
    }
}
