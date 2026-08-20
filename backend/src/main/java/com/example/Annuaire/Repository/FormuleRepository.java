package com.example.Annuaire.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Annuaire.Models.Formule;
import com.example.Annuaire.Models.FraudAlert;

public interface FormuleRepository extends JpaRepository<Formule, Long> {
    
}
