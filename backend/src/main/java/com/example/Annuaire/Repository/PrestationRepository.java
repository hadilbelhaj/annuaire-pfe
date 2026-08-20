package com.example.Annuaire.Repository;

import java.util.Optional;

import com.example.Annuaire.Models.Prestation;
import com.example.Annuaire.enums.Enums;
import com.example.Annuaire.enums.Enums.Prestation_libelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PrestationRepository extends JpaRepository<Prestation, Long> {

    @Query("SELECT p FROM Prestation p WHERE p.prestation_libelle = :libelle ORDER BY p.id ASC LIMIT 1")
    Optional<Prestation> findByPrestationLibelle(@Param("libelle") Enums.Prestation_libelle prestation_libelle);

}
