package com.example.Annuaire.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Annuaire.Models.Movement;
import com.example.DTOS.ps.HealthcareProfessionalStatsDTO;
import com.example.DTOS.ps.PrestationBySpecialtyDTO;
import com.example.DTOS.ps.PrestationMonthlyStatsDTO;
import com.example.DTOS.ps.PrestationStatsDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository

public interface PrestationStatisticsRepository extends JpaRepository<Movement, Long> {
        @Query(value = "SELECT prestation_id as prestationId, prestation_name as prestationName, " +
                        "frequency, total_revenue as totalRevenue, average_amount as averageAmount " +
                        "FROM v_most_frequent_prestations " +
                        "LIMIT :limit", nativeQuery = true)
        List<PrestationStatsDTO> findMostFrequentPrestationsFromView(@Param("limit") int limit);

        @Query(value = "SELECT prestation_id as prestationId, prestation_name as prestationName, " +
                        "frequency, total_revenue as totalRevenue, average_amount as averageAmount " +
                        "FROM v_most_frequent_prestations " +
                        "ORDER BY total_revenue DESC " +
                        "LIMIT :limit", nativeQuery = true)
        List<PrestationStatsDTO> findTopRevenuePrestationsFromView(@Param("limit") int limit);

        @Query(value = "SELECT prestation_id as prestationId, prestation_name as prestationName, " +
                        "year, month, frequency, total_revenue as totalRevenue, average_amount as averageAmount " +
                        "FROM v_monthly_prestation_stats " +
                        "WHERE prestation_id = :prestationId " +
                        "AND (year > :startYear OR (year = :startYear AND month >= :startMonth)) " +
                        "AND (year < :endYear OR (year = :endYear AND month <= :endMonth)) " +
                        "ORDER BY year, month", nativeQuery = true)
        List<PrestationMonthlyStatsDTO> findMonthlyPrestationFrequencyFromView(
                        @Param("prestationId") Long prestationId,
                        @Param("startYear") int startYear,
                        @Param("startMonth") int startMonth,
                        @Param("endYear") int endYear,
                        @Param("endMonth") int endMonth);

        @Query(value = "SELECT id, name, specialty, region, total_movements as totalMovements, " +
                        "total_revenue as totalRevenue, average_amount as averageAmount, " +
                        "distinct_prestations as distinctPrestations " +
                        "FROM v_healthcare_professional_stats " +
                        "WHERE (:specialty IS NULL OR specialty = :specialty) " +
                        "AND (:region IS NULL OR region = :region) " +
                        "ORDER BY total_movements DESC", countQuery = "SELECT COUNT(*) " +
                                        "FROM v_healthcare_professional_stats " +
                                        "WHERE (:specialty IS NULL OR specialty = :specialty) " +
                                        "AND (:region IS NULL OR region = :region)", nativeQuery = true)
        Page<HealthcareProfessionalStatsDTO> findHealthcareProfessionalStatsFromView(
                        @Param("specialty") String specialty,
                        @Param("region") String region,
                        Pageable pageable);

        @Query(value = "SELECT specialty, prestation_id as prestationId, prestation_name as prestationName, " +
                        "frequency, total_revenue as totalRevenue " +
                        "FROM v_prestations_by_specialty " +
                        "WHERE specialty = :specialty " +
                        "ORDER BY frequency DESC " +
                        "LIMIT :limit", nativeQuery = true)
        List<PrestationBySpecialtyDTO> findTopPrestationsBySpecialty(
                        @Param("specialty") String specialty,
                        @Param("limit") int limit);

}
