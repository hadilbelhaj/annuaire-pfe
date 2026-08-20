package com.example.Annuaire.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Annuaire.Models.FraudAlert;

@Repository
public interface FraudAlertRepository extends JpaRepository<FraudAlert, Long> {
    List<FraudAlert> findByStatus(FraudAlert.AlertStatus status);

    List<FraudAlert> findByReimbursementId(Long reimbursementId);
}