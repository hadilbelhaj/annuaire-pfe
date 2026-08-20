package com.example.Annuaire.Service.Statistiques;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import com.example.Annuaire.Repository.PrecalculatedStatsRepository;
import com.example.Annuaire.Models.PrecalculatedProfessionalStats;
import com.example.DTOS.ps.HealthcareProfessionalStatsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
public class StatsRetrievalService {
    private final PrecalculatedStatsRepository precalculatedStatsRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public StatsRetrievalService(
            PrecalculatedStatsRepository precalculatedStatsRepository,
            ObjectMapper objectMapper) {
        this.precalculatedStatsRepository = precalculatedStatsRepository;
        this.objectMapper = objectMapper;
    }

    public HealthcareProfessionalStatsDTO getLatestStats() {
        return precalculatedStatsRepository.findFirstByOrderByCalculatedAtDesc()
                .map(this::convertJsonToStats)
                .orElseThrow(() -> new RuntimeException("No precalculated stats found"));
    }

    private HealthcareProfessionalStatsDTO convertJsonToStats(PrecalculatedProfessionalStats precalculatedStats) {
        try {
            return objectMapper.readValue(precalculatedStats.getStatsJson(), HealthcareProfessionalStatsDTO.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error parsing stats JSON", e);
        }
    }

    public boolean areStatsCurrent() {
        return precalculatedStatsRepository.findFirstByOrderByCalculatedAtDesc()
                .map(stats -> Duration.between(stats.getCalculatedAt(), LocalDateTime.now()).toHours() < 24)
                .orElse(false);
    }
}
