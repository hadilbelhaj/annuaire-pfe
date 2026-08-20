package com.example.Annuaire.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.Annuaire.Models.ActePS;
import com.example.Annuaire.Models.HealthcareProfessional;

import jakarta.transaction.Transactional;

public interface ActePsRepository extends JpaRepository<ActePS, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ActePS a set a.libelle_actePs=:libelle where a.id=:id ")
    int updatelibelle(String libelle, Long id);

    List<ActePS> findByHealthcareProfessionalId(Long healthcareProfessionalId);

    List<ActePS> findByHealthcareProfessional(HealthcareProfessional healthcareProfessional);

}
