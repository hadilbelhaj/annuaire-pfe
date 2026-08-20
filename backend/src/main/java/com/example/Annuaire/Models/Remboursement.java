package com.example.Annuaire.Models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "remboursement")
public class Remboursement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "movement_id")
    private Movement movement;

    @ManyToOne
    @JoinColumn(name = "adherant_id")
    private Adherant adherant;

    private BigDecimal amount;
    private BigDecimal specialtyAverageAmount;
    private Integer reimbursementPercentage;
    private BigDecimal insuranceAmount;
    private BigDecimal adherantAmount;
    private LocalDateTime date;

    // Constructors
    public Remboursement() {
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Movement getMovement() {
        return movement;
    }

    public void setMovement(Movement movement) {
        this.movement = movement;
    }

    public Adherant getAdherant() {
        return adherant;
    }

    public void setAdherant(Adherant adherant) {
        this.adherant = adherant;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getSpecialtyAverageAmount() {
        return specialtyAverageAmount;
    }

    public void setSpecialtyAverageAmount(BigDecimal specialtyAverageAmount) {
        this.specialtyAverageAmount = specialtyAverageAmount;
    }

 

    public Integer getReimbursementPercentage() {
        return reimbursementPercentage;
    }

    public void setReimbursementPercentage(Integer reimbursementPercentage) {
        this.reimbursementPercentage = reimbursementPercentage;
    }

    public BigDecimal getInsuranceAmount() {
        return insuranceAmount;
    }

    public void setInsuranceAmount(BigDecimal insuranceAmount) {
        this.insuranceAmount = insuranceAmount;
    }

    public BigDecimal getAdherantAmount() {
        return adherantAmount;
    }

    public void setAdherantAmount(BigDecimal adherantAmount) {
        this.adherantAmount = adherantAmount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}