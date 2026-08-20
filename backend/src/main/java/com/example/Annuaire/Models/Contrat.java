package com.example.Annuaire.Models;

import java.util.Date;

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


@NoArgsConstructor
@Setter
@Getter
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date_debut;
    private Date date_fin;
    private Date date_resiliation;

    private String etat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contractant_id")
    private Contractant contractant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formule_id")
    private Formule formule;

}
