package com.example.Annuaire.Specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.Annuaire.Models.HealthcareProfessional;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

public class HealthcareProfessionalSpecification {

    public static Specification<HealthcareProfessional> searchBy(String searchTerm) {
        return (root, query, criteriaBuilder) -> {
            if (searchTerm == null || searchTerm.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();
            String pattern = "%" + searchTerm.toLowerCase() + "%";

            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("medicalSpecialty")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("region")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("numFiscal")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("number1")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("number2")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("numeroOrdre")), pattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("ref")), pattern));

            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }
}