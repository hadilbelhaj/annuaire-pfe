package com.example.Annuaire.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "historical_reimbursement_rates", uniqueConstraints = @UniqueConstraint(columnNames = { "formule_id",
        "prestation_id", "effective_from" }))
public class HistoricalReimbursementRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formule_id", nullable = false)
    private Formule formule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestation_id", nullable = false)
    private Prestation prestation;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    // Constructors
    public HistoricalReimbursementRate() {
    }

    public HistoricalReimbursementRate(Formule formule, Prestation prestation,
            BigDecimal percentage, LocalDate effectiveFrom) {
        this.formule = formule;
        this.prestation = prestation;
        this.percentage = percentage;
        this.effectiveFrom = effectiveFrom;
        this.createdAt = LocalDateTime.now();
    }
}
