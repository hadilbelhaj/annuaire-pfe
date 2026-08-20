package com.example.Annuaire.Repository;

import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.Movement;
import com.example.DTOS.ps.RegionDistributionDTO;
import com.example.DTOS.ps.SpecialtyDistributionDTO;
import com.example.DTOS.ps.TopProfessionalDTO;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;

@Repository
public interface HealthcareProfessionalRepository
                extends JpaRepository<HealthcareProfessional, Long>, JpaSpecificationExecutor<HealthcareProfessional> {
        @Query("SELECT DISTINCT h.medicalSpecialty FROM HealthcareProfessional h")
        List<String> findDistinctMedicalSpeciality();

        @Query("SELECT h FROM HealthcareProfessional h WHERE h.deleted = 0")
        List<HealthcareProfessional> findAllActive();

        @Query("SELECT h FROM HealthcareProfessional h WHERE h.id = :id AND h.deleted = 0")
        Optional<HealthcareProfessional> findByIdIfNotDeleted(Long id);

        @Query("SELECT DISTINCT h.region FROM HealthcareProfessional h")
        List<String> findDistinctRegion();

        @Query("SELECT count(*) FROM HealthcareProfessional")
        int countAllById();

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%'))")
        Page<HealthcareProfessional> searchProfessionals(String search, Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :search, '%')) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsByconventionne(String search, int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.region) = LOWER(:region)")
        Page<HealthcareProfessional> searchProfessionalsByRegion(String region, Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.region) = LOWER(:region) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsByRegionAndconventionne(String region, int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.medicalSpecialty) = LOWER(:specialty)")
        Page<HealthcareProfessional> searchProfessionalsBySpecialty(String specialty, Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.medicalSpecialty) = LOWER(:specialty) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsBySpecialtyAndconventionne(String specialty, int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND LOWER(h.region) = LOWER(:region) AND LOWER(h.medicalSpecialty) = LOWER(:specialty)")
        Page<HealthcareProfessional> searchProfessionalsBySpecialtyRegionName(
                        @Param("region") String region,
                        @Param("specialty") String specialty,
                        @Param("name") String name,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND LOWER(h.region) = LOWER(:region) AND LOWER(h.medicalSpecialty) = LOWER(:specialty) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsBySpecialtyRegionNameAndconventionne(
                        @Param("region") String region,
                        @Param("specialty") String specialty,
                        @Param("name") String name,
                        @Param("conventionne") int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.medicalSpecialty) = LOWER(:specialty)" +
                        "AND LOWER(h.region) = LOWER(:region)")
        Page<HealthcareProfessional> searchProfessionalsBySpecialtyRegion(
                        @Param("specialty") String specialty,
                        @Param("region") String region,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.medicalSpecialty) = LOWER(:specialty)" +
                        "AND LOWER(h.region) = LOWER(:region) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsBySpecialtyRegionAndconventionne(
                        @Param("specialty") String specialty,
                        @Param("region") String region,
                        @Param("conventionne") int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND LOWER(h.region) = LOWER(:region)")
        Page<HealthcareProfessional> searchProfessionalsByNameRegion(
                        @Param("region") String region,
                        @Param("name") String name,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND LOWER(h.region) = LOWER(:region) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsByNameRegionAndconventionne(
                        @Param("region") String region,
                        @Param("name") String name,
                        @Param("conventionne") int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND LOWER(h.medicalSpecialty) = LOWER(:specialty)")
        Page<HealthcareProfessional> searchProfessionalsByNameSpecialty(
                        @Param("specialty") String specialty,
                        @Param("name") String name,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
                        "AND LOWER(h.medicalSpecialty) = LOWER(:specialty) AND h.conventionne = :conventionne")
        Page<HealthcareProfessional> searchProfessionalsByNameSpecialtyAndconventionne(
                        @Param("specialty") String specialty,
                        @Param("name") String name,
                        @Param("conventionne") int conventionne,
                        Pageable pageable);

        @Query("SELECT h FROM HealthcareProfessional h WHERE h.conventionne = :conventionne")
        Page<HealthcareProfessional> findAllByconventionne(@Param("conventionne") int conventionne, Pageable pageable);

        @Transactional
        @Modifying
        @Query("UPDATE HealthcareProfessional h SET h.ref= :ref WHERE h.id = :id")
        void updateRef(@Param("id") Long id, @Param("ref") String ref);

        @Transactional
        @Modifying
        @Query("UPDATE HealthcareProfessional h SET h.numFiscal= :num WHERE h.id = :id")
        void updateNumFiscal(@Param("id") Long id, @Param("num") String num);

        @Query("SELECT new com.example.DTOS.ps.SpecialtyDistributionDTO(h.medicalSpecialty, COUNT(h), " +
                        "COUNT(h) * 100.0 / (SELECT COUNT(hp) FROM HealthcareProfessional hp)) " +
                        "FROM HealthcareProfessional h GROUP BY h.medicalSpecialty ORDER BY COUNT(h) DESC")
        List<SpecialtyDistributionDTO> countBySpecialty();

        @Query("SELECT new com.example.DTOS.ps.RegionDistributionDTO(h.region, COUNT(h), " +
                        "COUNT(h) * 100.0 / (SELECT COUNT(hp) FROM HealthcareProfessional hp)) " +
                        "FROM HealthcareProfessional h GROUP BY h.region ORDER BY COUNT(h) DESC")
        List<RegionDistributionDTO> countByRegion();

        @Query("SELECT h.id as id, h.name as name, h.medicalSpecialty as medicalSpecialty, h.region as region, " +
                        "COUNT(m) as visitCount, COUNT(m) as transactionCount, " +
                        "SUM(m.amount) as totalAmount, AVG(m.amount) as averageAmount " +
                        "FROM HealthcareProfessional h JOIN Movement m ON h.id = m.healthcareProfessional.id " +
                        "GROUP BY h.id, h.name, h.medicalSpecialty, h.region " +
                        "ORDER BY COUNT(m) DESC")
        List<Object[]> findTopByVisitCountRaw(Pageable pageable);

        @Query("SELECT h.id as id, h.name as name, h.medicalSpecialty as medicalSpecialty, h.region as region, " +
                        "COUNT(m) as visitCount, COUNT(m) as transactionCount, " +
                        "SUM(m.amount) as totalAmount, AVG(m.amount) as averageAmount " +
                        "FROM HealthcareProfessional h JOIN Movement m ON h.id = m.healthcareProfessional.id " +
                        "GROUP BY h.id, h.name, h.medicalSpecialty, h.region " +
                        "ORDER BY SUM(m.amount) DESC")
        List<Object[]> findTopByTransactionVolumeRaw(Pageable pageable);

        @Query("SELECT h.id as id, h.name as name, h.medicalSpecialty as medicalSpecialty, h.region as region, " +
                        "COUNT(m) as visitCount, COUNT(m) as transactionCount, " +
                        "SUM(m.amount) as totalAmount, AVG(m.amount) as averageAmount " +
                        "FROM HealthcareProfessional h JOIN Movement m ON h.id = m.healthcareProfessional.id " +
                        "GROUP BY h.id, h.name, h.medicalSpecialty, h.region " +
                        "ORDER BY AVG(m.amount) DESC")
        List<Object[]> findTopByAverageAmountRaw(Pageable pageable);

        @Query("SELECT COUNT(m), SUM(m.amount), AVG(m.amount) " +
                        "FROM Movement m WHERE m.healthcareProfessional.id = :id")
        Object[] getTransactionInfoForProfessional(@Param("id") Long id);

        // first new api
        @Query(value = "SELECT hp.healthcare_professional_id, hp.name, hp.address, hp.latitude, hp.longitude, " +
                        "ST_Distance_Sphere(hp.location, POINT(:longitude, :latitude)) as distance " +
                        "FROM healthcare_professional hp " +
                        "WHERE hp.deleted = 0 " +
                        "AND hp.latitude IS NOT NULL AND hp.longitude IS NOT NULL " +
                        "AND ST_Distance_Sphere(hp.location, POINT(:longitude, :latitude)) <= :maxDistance " +
                        "ORDER BY distance", nativeQuery = true)
        Page<Object[]> findNearbyProfessionals(
                        @Param("latitude") double latitude,
                        @Param("longitude") double longitude,
                        @Param("maxDistance") double maxDistanceMeters,
                        Pageable pageable);

        @Query(value = "SELECT hp.healthcare_professional_id, hp.name, hp.address, hp.latitude, hp.longitude, " +
                        "ST_Distance_Sphere(hp.location, POINT(:longitude, :latitude)) as distance " +
                        "FROM healthcare_professional hp " +
                        "JOIN acteps ap ON hp.healthcare_professional_id = ap.healthcare_professional_id " +
                        "JOIN prestation p ON ap.prestation_id = p.id " +
                        "WHERE hp.deleted = 0 " +
                        "AND hp.latitude IS NOT NULL AND hp.longitude IS NOT NULL " +
                        "AND p.prestation_libelle = :prestation " +
                        "AND ST_Distance_Sphere(hp.location, POINT(:longitude, :latitude)) <= :maxDistance " +
                        "GROUP BY hp.healthcare_professional_id " +
                        "ORDER BY distance", nativeQuery = true)
        Page<Object[]> findNearbyProfessionalsByPrestation(
                        @Param("latitude") double latitude,
                        @Param("longitude") double longitude,
                        @Param("prestation") String prestation,
                        @Param("maxDistance") double maxDistanceMeters,
                        Pageable pageable);

        @Query("SELECT h.id FROM HealthcareProfessional h WHERE LOWER(h.name) = LOWER(:name)")
        Long findHealthcareProfessionalIdByName(@Param("name") String name);

        Optional<HealthcareProfessional> findByName(String psName);
}
