package com.example.Annuaire.Service.Statistiques;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.DTOS.MouvementParPs.AdherentStatsDTO;
import com.example.DTOS.MouvementParPs.MonthlyStatsDTO;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.MouvementParPs.MovementStatisticsDTO;

@Service

public class MouvementsParPsStats {
        @Autowired
        private MovementRepository movementRepository;

        public List<MovementDTO> getMovementsByHealthcareProfessionalId(Long healthcareProfessionalId) {
                List<Movement> movements = movementRepository.findByHealthcareProfessionalId(
                                healthcareProfessionalId);
                return movements.stream()
                                .map(this::convertToDTO)
                                .collect(Collectors.toList());
        }

        public MovementStatisticsDTO getStatisticsForHealthcareProfessional(Long healthcareProfessionalId) {
                List<Movement> movements = movementRepository.findByHealthcareProfessionalId(
                                healthcareProfessionalId);

                MovementStatisticsDTO stats = new MovementStatisticsDTO();

                stats.setTotalMovements(movements.size());

                BigDecimal totalAmount = movements.stream()
                                .map(Movement::getAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
                stats.setTotalAmount(totalAmount);

                if (!movements.isEmpty()) {
                        stats.setAverageAmount(totalAmount.divide(
                                        BigDecimal.valueOf(movements.size()), 2, RoundingMode.HALF_UP));

                        stats.setMinAmount(movements.stream()
                                        .min(Comparator.comparing(Movement::getAmount))
                                        .map(Movement::getAmount)
                                        .orElse(BigDecimal.ZERO));

                        stats.setMaxAmount(movements.stream()
                                        .max(Comparator.comparing(Movement::getAmount))
                                        .map(Movement::getAmount)
                                        .orElse(BigDecimal.ZERO));
                        stats.setMostRecentTransactionDate(movements.stream()
                                        .max(Comparator.comparing(Movement::getDate))
                                        .map(Movement::getDate)
                                        .orElse(null));
                        stats.setUniqueAdherentsCount((int) movements.stream()
                                        .map(m -> m.getAdherant().getId())
                                        .distinct()
                                        .count());
                }

                return stats;
        }

        public List<MonthlyStatsDTO> getMonthlyStatsForHealthcareProfessional(
                        Long healthcareProfessionalId, int year) {

                List<Movement> movements = movementRepository.findByHealthcareProfessionalIdAndYear(
                                healthcareProfessionalId, year);

                Map<Month, List<Movement>> movementsByMonth = movements.stream()
                                .collect(Collectors.groupingBy(m -> m.getDate().getMonth()));

                List<MonthlyStatsDTO> result = new ArrayList<>();

                for (Month month : Month.values()) {
                        MonthlyStatsDTO monthStats = new MonthlyStatsDTO();
                        monthStats.setMonth(month.toString());
                        monthStats.setYear(year);

                        List<Movement> monthMovements = movementsByMonth.getOrDefault(month, new ArrayList<>());
                        monthStats.setTransactionCount(monthMovements.size());

                        BigDecimal monthlyTotal = monthMovements.stream()
                                        .map(Movement::getAmount)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        monthStats.setTotalAmount(monthlyTotal);

                        result.add(monthStats);
                }

                return result;
        }

        public List<AdherentStatsDTO> getAdherentDistributionForHealthcareProfessional(
                        Long healthcareProfessionalId) {

                List<Movement> movements = movementRepository.findByHealthcareProfessionalId(
                                healthcareProfessionalId);

                Map<Long, List<Movement>> movementsByAdherent = movements.stream()
                                .collect(Collectors.groupingBy(m -> m.getAdherant().getId()));

                return movementsByAdherent.entrySet().stream()
                                .map(entry -> {
                                        AdherentStatsDTO stats = new AdherentStatsDTO();
                                        Long adherentId = entry.getKey();
                                        List<Movement> adherentMovements = entry.getValue();

                                        stats.setAdherentId(adherentId);
                                        stats.setAdherentName(adherentMovements.get(0).getAdherant().getName());
                                        stats.setTransactionCount(adherentMovements.size());

                                        BigDecimal totalAmount = adherentMovements.stream()
                                                        .map(Movement::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        stats.setTotalAmount(totalAmount);

                                        stats.setLastTransactionDate(adherentMovements.stream()
                                                        .max(Comparator.comparing(Movement::getDate))
                                                        .map(Movement::getDate)
                                                        .orElse(null));

                                        return stats;
                                })
                                .collect(Collectors.toList());
        }

        private MovementDTO convertToDTO(Movement movement) {
                MovementDTO dto = new MovementDTO();
                dto.setId(movement.getId());
                dto.setAmount(movement.getAmount());
                dto.setDate(movement.getDate());
                dto.setDescription(movement.getDescription());
                dto.setAdherentId(movement.getAdherant().getId());
                dto.setAdherentName(movement.getAdherant().getName());
                return dto;
        }
}
