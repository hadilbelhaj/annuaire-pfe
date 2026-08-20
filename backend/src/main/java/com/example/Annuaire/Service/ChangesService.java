package com.example.Annuaire.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.ChangesPs;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;

@Service
public class ChangesService {
    private final HealthcareProfessionalRepository repository;

    @Value("${node.changes.folder}")
    private String changesFolder;

    public ChangesService(HealthcareProfessionalRepository repository) {
        this.repository = repository;
    }

    private ChangesPs readChangesFromJson(String filePath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

        String content = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);

        return objectMapper.readValue(content, ChangesPs.class);
    }

    public void applyChanges(String todaString) throws IOException {
        System.err.println(changesFolder + "/db-changes-" + todaString + ".json");
        ChangesPs changes = readChangesFromJson(changesFolder + "/db-changes-" + todaString + ".json");
        processChanges(changes);
    }

    private void processChanges(ChangesPs changes) {
        processInserts(changes.getInserts());
        processUpdates(changes.getUpdates());
        processDeletes(changes.getDeletes());
    }

    private void processInserts(List<HealthcareProfessional> inserts) {
        for (HealthcareProfessional healthcareProfessional : inserts) {
            repository.save(healthcareProfessional);
        }
    }

    private void processUpdates(List<HealthcareProfessional> updates) {
        for (HealthcareProfessional healthcareProfessional : updates) {
            repository.save(healthcareProfessional);
        }
    }

    private void processDeletes(List<HealthcareProfessional> deletes) {
        for (HealthcareProfessional healthcareProfessional : deletes) {
            repository.delete(healthcareProfessional);
        }
    }

}
