package com.example.Annuaire.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Annuaire.Models.Remboursement;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, Long> {

    // In your RemboursementRepository interface
    List<Remboursement> findByMovementDateBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);
}