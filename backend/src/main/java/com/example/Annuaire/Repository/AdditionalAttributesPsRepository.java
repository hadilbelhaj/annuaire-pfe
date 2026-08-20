package com.example.Annuaire.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Annuaire.Models.AdditionalAttributesPs;

@Repository
public interface AdditionalAttributesPsRepository extends JpaRepository<AdditionalAttributesPs, Long> {

}
