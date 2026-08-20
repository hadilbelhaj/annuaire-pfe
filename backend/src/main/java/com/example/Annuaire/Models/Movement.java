package com.example.Annuaire.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Getter
@Setter

@NoArgsConstructor
public class Movement {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "adherant_id", nullable = false)
        private Adherant adherant;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "healthcare_professional_id", nullable = false)
        private HealthcareProfessional healthcareProfessional;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "acteps_id")
        private ActePS actePS;

        private BigDecimal total;

        private BigDecimal amount;

        private LocalDateTime date;

        private String description;

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public Adherant getAdherant() {
                return adherant;
        }

        public void setAdherant(Adherant adherant) {
                this.adherant = adherant;
        }

        public HealthcareProfessional getHealthcareProfessional() {
                return healthcareProfessional;
        }

        public void setHealthcareProfessional(HealthcareProfessional healthcareProfessional) {
                this.healthcareProfessional = healthcareProfessional;
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

}
