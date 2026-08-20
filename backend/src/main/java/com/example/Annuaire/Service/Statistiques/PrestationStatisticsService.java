package com.example.Annuaire.Service.Statistiques;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Repository.PrestationStatisticsRepository;
import com.example.DTOS.ps.HealthcareProfessionalStatsDTO;
import com.example.DTOS.ps.PrestationBySpecialtyDTO;
import com.example.DTOS.ps.PrestationMonthlyStatsDTO;
import com.example.DTOS.ps.PrestationStatsDTO;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestationStatisticsService {

    @Autowired
    private PrestationStatisticsRepository repository;

    public List<PrestationStatsDTO> getMostFrequentPrestations(int limit) {
        return repository.findMostFrequentPrestationsFromView(limit);
    }

    public List<PrestationStatsDTO> getTopRevenuePrestations(int limit) {
        return repository.findTopRevenuePrestationsFromView(limit);
    }

    public List<PrestationMonthlyStatsDTO> getMonthlyPrestationStats(
            Long prestationId, LocalDate startDate, LocalDate endDate) {

        return repository.findMonthlyPrestationFrequencyFromView(
                prestationId,
                startDate.getYear(),
                startDate.getMonthValue(),
                endDate.getYear(),
                endDate.getMonthValue());
    }

    public Page<HealthcareProfessionalStatsDTO> getHealthcareProfessionalStats(
            String specialty, String region, Pageable pageable) {

        return repository.findHealthcareProfessionalStatsFromView(specialty, region, pageable);
    }

    public List<PrestationBySpecialtyDTO> getTopPrestationsBySpecialty(
            String specialty, int limit) {

        return repository.findTopPrestationsBySpecialty(specialty, limit);
    }
}
