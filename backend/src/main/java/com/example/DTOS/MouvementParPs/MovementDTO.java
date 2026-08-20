package com.example.DTOS.MouvementParPs;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.Annuaire.Models.ActePS;
import com.example.Annuaire.enums.Enums.Prestation_libelle;
import com.example.DTOS.HealthcareProfessionalDTO;
import com.example.DTOS.ps.ActePSDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovementDTO {
    private Long id;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
    private Long adherentId;
    private String adherentName;
    private String healthcareProfessionalName;
    private String actePSName;
    private HealthcareProfessionalDTO healthcareProfessional;
    private ActePSDTO actePS;

    public MovementDTO() {
    }

    public MovementDTO(Long id, BigDecimal amount, LocalDateTime date, String description, Long adherentId,
            String adherentName) {
        this.id = id;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.adherentId = adherentId;
        this.adherentName = adherentName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAdherentId() {
        return adherentId;
    }

    public void setAdherentId(Long adherentId) {
        this.adherentId = adherentId;
    }

    public String getAdherentName() {
        return adherentName;
    }

    public void setAdherentName(String adherentName) {
        this.adherentName = adherentName;
    }

    public HealthcareProfessionalDTO getHealthcareProfessional() {
        return healthcareProfessional;
    }

    public ActePSDTO getActePS() {
        return actePS;
    }

}
