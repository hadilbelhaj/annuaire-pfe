package com.example.Annuaire.Service.Statistiques;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.Annuaire.Repository.PrecalculatedStatsRepository;
import com.example.Annuaire.Models.PrecalculatedProfessionalStats;
import com.example.DTOS.ps.HealthcareProfessionalStatsDTO;
import com.example.DTOS.ps.RegionDistributionDTO;
import com.example.DTOS.ps.SpecialtyDistributionDTO;
import com.example.DTOS.ps.TopProfessionalDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.stream.Collectors;




@Service
public class HealthcareProfessionalStatsService {

    private static final Logger logger = LoggerFactory.getLogger(HealthcareProfessionalStatsService.class);

    private final HealthcareProfessionalRepository healthcareProfessionalRepository;
    private final MovementRepository movementRepository;
    private final PrecalculatedStatsRepository precalculatedStatsRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public HealthcareProfessionalStatsService(
            HealthcareProfessionalRepository healthcareProfessionalRepository,
            MovementRepository movementRepository,
            PrecalculatedStatsRepository precalculatedStatsRepository,
            ObjectMapper objectMapper) {
        this.healthcareProfessionalRepository = healthcareProfessionalRepository;
        this.movementRepository = movementRepository;
        this.precalculatedStatsRepository = precalculatedStatsRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void calculateAndPersistStats() {
        try {
            // Create a new stats DTO
            HealthcareProfessionalStatsDTO stats = new HealthcareProfessionalStatsDTO();

            // Calculate specialty distribution
            List<SpecialtyDistributionDTO> specialtyDistribution = healthcareProfessionalRepository.countBySpecialty();
            stats.setSpecialtyDistribution(specialtyDistribution);

            // Calculate region distribution
            List<RegionDistributionDTO> regionDistribution = healthcareProfessionalRepository.countByRegion();
            stats.setRegionDistribution(regionDistribution);

            // Calculate top professionals by different metrics
            Pageable topFive = PageRequest.of(0, 5);
            stats.setTopByVisits(getTopByVisitCount(topFive));
            stats.setTopByTransactionVolume(getTopByTransactionVolume(topFive));
            stats.setTopByAverageAmount(getTopByAverageAmount(topFive));

            // Convert stats to JSON
            String statsJson = objectMapper.writeValueAsString(stats);

            // Create and save precalculated stats entity
            PrecalculatedProfessionalStats precalculatedStats = new PrecalculatedProfessionalStats();
            precalculatedStats.setStatsJson(statsJson);
            precalculatedStats.setCalculatedAt(LocalDateTime.now());

            precalculatedStatsRepository.save(precalculatedStats);

            logger.info("Successfully calculated and persisted professional stats");
        } catch (Exception e) {
            logger.error("Error calculating professional stats", e);
            throw new RuntimeException("Failed to calculate stats", e);
        }
    }

    // Reuse existing methods from your current stats service
    private List<TopProfessionalDTO> getTopByVisitCount(Pageable pageable) {
        List<Object[]> results = healthcareProfessionalRepository.findTopByVisitCountRaw(pageable);
        return results.stream()
                .map(result -> convertToDTO(result))
                .collect(Collectors.toList());
    }

    private List<TopProfessionalDTO> getTopByTransactionVolume(Pageable pageable) {
        List<Object[]> results = healthcareProfessionalRepository.findTopByTransactionVolumeRaw(pageable);
        return results.stream()
                .map((Object[] result) -> convertToDTO(result))
                .collect(Collectors.toList());
    }

    private List<TopProfessionalDTO> getTopByAverageAmount(Pageable pageable) {
        List<Object[]> results = healthcareProfessionalRepository.findTopByAverageAmountRaw(pageable);
        return results.stream()
                .map(result -> convertToDTO(result))
                .collect(Collectors.toList());
    }

    private TopProfessionalDTO convertToDTO(Object[] result) {
        // Reuse the existing conversion method from your stats service
        TopProfessionalDTO dto = new TopProfessionalDTO(
                (Long) result[0], // id
                (String) result[1], // name
                (String) result[2], // medicalSpecialty
                (String) result[3] // region
        );
        dto.setVisitCount((Long) result[4]);
        dto.setTransactionCount((Long) result[5]);

        // Handle numeric conversions safely
        try {
            dto.setTotalAmount(result[6] != null ? new BigDecimal(result[6].toString()) : null);
            dto.setAverageAmount(result[7] != null ? new BigDecimal(result[7].toString()) : null);
        } catch (NumberFormatException e) {
            logger.warn("Number conversion error in stats calculation", e);
        }

        return dto;
    }
}
