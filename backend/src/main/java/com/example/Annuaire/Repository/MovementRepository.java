package com.example.Annuaire.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Annuaire.Models.ActePS;
import com.example.Annuaire.Models.Movement;

@Repository
public interface MovementRepository extends JpaRepository<Movement, Long> {
    List<Movement> findByActePS(ActePS actePS);
    List<Movement> findByHealthcareProfessionalNameContainingIgnoreCase(String name);

    @Query("SELECT m FROM Movement m WHERE m.actePS.healthcareProfessional.id = :healthcareProfessionalId")
    List<Movement> findByHealthcareProfessionalId(@Param("healthcareProfessionalId") Long healthcareProfessionalId);

    @Query("SELECT m FROM Movement m WHERE m.actePS.healthcareProfessional.id = :healthcareProfessionalId " +
            "AND YEAR(m.date) = :year")
    List<Movement> findByHealthcareProfessionalIdAndYear(
            @Param("healthcareProfessionalId") Long healthcareProfessionalId,
            @Param("year") int year);

    @Query("SELECT m FROM Movement m WHERE m.actePS.healthcareProfessional.id = :healthcareProId " +
            "AND m.date BETWEEN :startDate AND :endDate ORDER BY m.date DESC")
    List<Movement> findByHealthcareProfessionalIdAndDateBetween(
            @Param("healthcareProId") Long healthcareProId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("""
                SELECT
                    CONCAT(a.firstName, ' ', a.lastName) AS adherantName,
                    a.deductible AS adherantDeductible,
                    a.email AS adherantEmail,
                    COUNT(m) AS totalVisits,
                    SUM(m.amount) AS totalAmount
                FROM Movement m
                LEFT JOIN m.adherant a
                WHERE (COALESCE(:search, '') = '' OR
                    LOWER(CONCAT(a.firstName, ' ', a.lastName)) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(a.email) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    CAST(a.deductible AS string) LIKE LOWER(CONCAT('%', :search, '%')))
                GROUP BY a
                ORDER BY
                    CASE WHEN :sortField = 'adherantName' AND :sortDir = 'asc' THEN CONCAT(a.firstName, ' ', a.lastName) END ASC,
                    CASE WHEN :sortField = 'adherantName' AND :sortDir = 'desc' THEN CONCAT(a.firstName, ' ', a.lastName) END DESC,
                    CASE WHEN :sortField = 'adherantDeductible' AND :sortDir = 'asc' THEN a.deductible END ASC,
                    CASE WHEN :sortField = 'adherantDeductible' AND :sortDir = 'desc' THEN a.deductible END DESC,
                    CASE WHEN :sortField = 'adherantEmail' AND :sortDir = 'asc' THEN a.email END ASC,
                    CASE WHEN :sortField = 'adherantEmail' AND :sortDir = 'desc' THEN a.email END DESC,
                    CASE WHEN :sortField = 'totalVisits' AND :sortDir = 'asc' THEN COUNT(m) END ASC,
                    CASE WHEN :sortField = 'totalVisits' AND :sortDir = 'desc' THEN COUNT(m) END DESC,
                    CASE WHEN :sortField = 'totalAmount' AND :sortDir = 'asc' THEN SUM(m.amount) END ASC,
                    CASE WHEN :sortField = 'totalAmount' AND :sortDir = 'desc' THEN SUM(m.amount) END DESC
            """)
    Page<Object[]> findMovementsGroupByAdherant(
            @Param("search") String search,
            @Param("sortField") String sortField,
            String sortDir,
            Pageable pageable);

    @Query("""
                SELECT
                    CONCAT(a.firstName, ' ', a.lastName) AS adherantName,
                    a.deductible AS adherantDeductible,
                    a.email AS adherantEmail,
                    COUNT(m) AS totalVisits,
                    SUM(m.amount) AS totalAmount
                FROM Movement m
                LEFT JOIN m.adherant a
                GROUP BY a
            """)
    List<Object[]> findAllMovementsGroupByAdherant();

    @Query("""
                SELECT
                    h.name AS healthcareProfessionalName,
                    h.medicalSpecialty AS medicalSpecialty,
                    h.ref AS ref,
                    COUNT(m) AS totalVisits,
                    SUM(m.amount) AS totalAmount
                FROM Movement m
                LEFT JOIN m.healthcareProfessional h
                WHERE (COALESCE(:search, '') = '' OR
                    LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(h.medicalSpecialty) LIKE LOWER(CONCAT('%', :search, '%')) OR
                    LOWER(h.ref) LIKE LOWER(CONCAT('%', :search, '%')))
                GROUP BY m.healthcareProfessional
                ORDER BY
                    CASE WHEN :sortField = 'healthcareProfessionalName' AND :sortDirection = 'asc' THEN h.name END ASC,
                    CASE WHEN :sortField = 'healthcareProfessionalName' AND :sortDirection = 'desc' THEN h.name END DESC,
                    CASE WHEN :sortField = 'medicalSpecialty' AND :sortDirection = 'asc' THEN h.medicalSpecialty END ASC,
                    CASE WHEN :sortField = 'medicalSpecialty' AND :sortDirection = 'desc' THEN h.medicalSpecialty END DESC,
                    CASE WHEN :sortField = 'ref' AND :sortDirection = 'asc' THEN h.ref END ASC,
                    CASE WHEN :sortField = 'ref' AND :sortDirection = 'desc' THEN h.ref END DESC,
                    CASE WHEN :sortField = 'totalVisits' AND :sortDirection = 'asc' THEN COUNT(m) END ASC,
                    CASE WHEN :sortField = 'totalVisits' AND :sortDirection = 'desc' THEN COUNT(m) END DESC,
                    CASE WHEN :sortField = 'totalAmount' AND :sortDirection = 'asc' THEN SUM(m.amount) END ASC,
                    CASE WHEN :sortField = 'totalAmount' AND :sortDirection = 'desc' THEN SUM(m.amount) END DESC
            """)
    Page<Object[]> findMovementsGroupByHealthCareProfessional(
            @Param("search") String search,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection,
            Pageable pageable);

    @Query("""
                SELECT
                    h.name AS healthcareProfessionalName,
                    h.medicalSpecialty AS medicalSpecialty,
                    h.ref AS ref,
                    COUNT(m) AS totalVisits,
                    SUM(m.amount) AS totalAmount
                FROM Movement m
                LEFT JOIN m.healthcareProfessional h
                GROUP BY m.healthcareProfessional
            """)
    List<Object[]> findAllMovementsGroupByHealthCareProfessional();

    @Query("""
                        SELECT
                            CASE
                                WHEN :period = 'day' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                                WHEN :period = 'month' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m')
                                WHEN :period = 'quarter' THEN CONCAT(YEAR(m.date), '-Q', QUARTER(m.date))
                                WHEN :period = 'year' THEN CAST(YEAR(m.date) AS string)
                                ELSE FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                            END AS datePeriod,
                            COUNT(m) AS totalVisits,
                            SUM(m.amount) AS totalAmount
                        FROM Movement m
                        WHERE (COALESCE(:search, '') = '' OR
                            LOWER(FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :search, '%')))
                        GROUP BY
            CASE
                WHEN :period = 'day' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                WHEN :period = 'month' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m')
                WHEN :period = 'quarter' THEN CONCAT(YEAR(m.date), '-Q', QUARTER(m.date))
                WHEN :period = 'year' THEN CAST(YEAR(m.date) AS string)
                ELSE FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
            END
                        ORDER BY
                            CASE WHEN :sortField = 'datePeriod' AND :sortDirection = 'asc' THEN
                                CASE
                                    WHEN :period = 'day' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                                    WHEN :period = 'month' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m')
                                    WHEN :period = 'quarter' THEN CONCAT(YEAR(m.date), '-Q', QUARTER(m.date))
                                    WHEN :period = 'year' THEN CAST(YEAR(m.date) AS string)
                                    ELSE FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                                END
                            END ASC,
                            CASE WHEN :sortField = 'datePeriod' AND :sortDirection = 'desc' THEN
                                CASE
                                    WHEN :period = 'day' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                                    WHEN :period = 'month' THEN FUNCTION('DATE_FORMAT', m.date, '%Y-%m')
                                    WHEN :period = 'quarter' THEN CONCAT(YEAR(m.date), '-Q', QUARTER(m.date))
                                    WHEN :period = 'year' THEN CAST(YEAR(m.date) AS string)
                                    ELSE FUNCTION('DATE_FORMAT', m.date, '%Y-%m-%d')
                                END
                            END DESC,
                            CASE WHEN :sortField = 'totalVisits' AND :sortDirection = 'asc' THEN COUNT(m) END ASC,
                            CASE WHEN :sortField = 'totalVisits' AND :sortDirection = 'desc' THEN COUNT(m) END DESC,
                            CASE WHEN :sortField = 'totalAmount' AND :sortDirection = 'asc' THEN SUM(m.amount) END ASC,
                            CASE WHEN :sortField = 'totalAmount' AND :sortDirection = 'desc' THEN SUM(m.amount) END DESC
                    """)
    Page<Object[]> findMovementsGroupByDate(
            @Param("period") String period,
            @Param("search") String search,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection,
            Pageable pageable);

    @Query(value = """
            SELECT
                CASE
                    WHEN amount < 100 THEN '0-100'
                    WHEN amount BETWEEN 100 AND 500 THEN '100-500'
                    ELSE '500+'
                END AS category,
                COUNT(*) AS value
            FROM Movement m
            GROUP BY category
            """, nativeQuery = true)
    List<Map<String, Object>> getAmountDistribution();

    @Query(value = """
            SELECT
                YEAR(date) AS year,
                MONTH(date) AS month,
                COUNT(*) AS count,
                SUM(amount) AS totalAmount
            FROM Movement m
            GROUP BY YEAR(date), MONTH(date)
            ORDER BY year DESC, month DESC
            """, nativeQuery = true)
    List<Map<String, Object>> getMonthlyTrend();

    @Query(value = """
                SELECT
                    hp.name AS healthcareProfessionalName,
                    COUNT(*) AS transactionCount,
                    SUM(m.amount) AS totalAmount
                FROM movement m
                LEFT JOIN healthcare_professional hp
                    ON m.healthcare_professional_id = hp.healthcare_professional_id  -- Explicit JOIN condition
                GROUP BY hp.healthcare_professional_id, hp.name  -- Group by ID and name for strict SQL modes
                ORDER BY transactionCount DESC
                LIMIT 10
            """, nativeQuery = true)
    List<Map<String, Object>> getTopProfessionals(@Param("limit") int limit);

    List<Movement> findByDateBetween(LocalDateTime startOfPeriod, LocalDateTime endOfPeriod);

    @Query("SELECT AVG(m.amount) FROM Movement m JOIN m.healthcareProfessional hp WHERE hp.medicalSpecialty = ?1")
    BigDecimal findAverageAmountBySpecialty(String specialty);

    @Query(value = """
            SELECT m FROM Movement m
            LEFT JOIN m.healthcareProfessional hp
            LEFT JOIN m.adherant a
            WHERE CONCAT(hp.name, ' ', hp.medicalSpecialty, ' ', m.amount) LIKE %:search% OR
                  CONCAT(a.firstName, ' ', a.lastName, ' ', a.email, ' ', a.deductible) LIKE %:search%
            ORDER BY
                CASE WHEN :sortField = 'healthcareProfessionalName' AND :sortDirection = 'asc' THEN hp.name END ASC,
                CASE WHEN :sortField = 'healthcareProfessionalName' AND :sortDirection = 'desc' THEN hp.name END DESC,
                CASE WHEN :sortField = 'medicalSpecialty' AND :sortDirection = 'asc' THEN hp.medicalSpecialty END ASC,
                CASE WHEN :sortField = 'medicalSpecialty' AND :sortDirection = 'desc' THEN hp.medicalSpecialty END DESC,
                CASE WHEN :sortField = 'amount' AND :sortDirection = 'asc' THEN m.amount END ASC,
                CASE WHEN :sortField = 'amount' AND :sortDirection = 'desc' THEN m.amount END DESC,
                CASE WHEN :sortField = 'date' AND :sortDirection = 'asc' THEN m.date END ASC,
                CASE WHEN :sortField = 'date' AND :sortDirection = 'desc' THEN m.date END DESC,
                CASE WHEN :sortField = 'adherant' AND :sortDirection = 'asc' THEN m.adherant END ASC,
                CASE WHEN :sortField = 'adherant' AND :sortDirection = 'desc' THEN m.adherant END DESC

            """)

    Page<Movement> findMovementsWithSearchAndSort(Pageable pageable,
            @Param("search") String search,
            @Param("sortField") String sortField,
            @Param("sortDirection") String sortDirection);

    List<Movement> findByAdherantId(Long adherantId);

    List<Movement> findByDateAfter(LocalDateTime date);

}
