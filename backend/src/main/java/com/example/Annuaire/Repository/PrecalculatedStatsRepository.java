package com.example.Annuaire.Repository;

import com.example.Annuaire.Models.PrecalculatedProfessionalStats;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrecalculatedStatsRepository extends JpaRepository<PrecalculatedProfessionalStats, Long> {
    Optional<PrecalculatedProfessionalStats> findFirstByOrderByCalculatedAtDesc();
}