package com.example.Annuaire.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.example.Annuaire.Models.Adherant;
import com.example.Annuaire.Models.HealthcareProfessional;

import jakarta.transaction.Transactional;

public interface AdherantRepository extends JpaRepository<Adherant, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE Adherant a SET a.contract.id = :contratId WHERE a.id = :adherantId")
    void setContratId(Long adherantId, Long contratId);

    @Modifying
    @Transactional
    @Query("UPDATE Adherant a SET a.deductible = :deductible WHERE a.id = :adherantId")
    void setDeductible(int deductible, Long adherantId);

    Optional<Adherant> findByFirstNameAndLastName(String firstName, String lastName);

}
