package com.example.Annuaire.Models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "precalculated_professional_stats")
public class PrecalculatedProfessionalStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stats_json", columnDefinition = "TEXT")
    private String statsJson;

    @Column(name = "calculated_at")
    private LocalDateTime calculatedAt;

    public PrecalculatedProfessionalStats() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatsJson() {
        return statsJson;
    }

    public void setStatsJson(String statsJson) {
        this.statsJson = statsJson;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public void setCalculatedAt(LocalDateTime calculatedAt) {
        this.calculatedAt = calculatedAt;
    }
}
