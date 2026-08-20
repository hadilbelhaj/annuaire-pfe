package com.example.Annuaire.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.Movement;
import com.example.Annuaire.Models.MovementMapper;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Repository.MovementRepository;
import com.example.DTOS.MovementsTable.GroupingByDateDto;
import com.example.DTOS.MovementsTable.GroupingByPsDto;
import com.example.DTOS.mouvement.MovementResponseDTO;
import com.example.DTOS.MouvementParPs.MovementDTO;
import com.example.DTOS.MovementsTable.GroupingByAdherantDto;

@Service
public class MovementService {
        @Autowired
        private final MovementRepository movementRepository;
        private final MovementMapper movementMapper;
        private final HealthcareProfessionalRepository hp;

        public MovementService(MovementRepository movementRepository, MovementMapper movementMapper,
                        HealthcareProfessionalRepository hp) {
                this.movementRepository = movementRepository;
                this.movementMapper = movementMapper;
                this.hp = hp;
        }

        public Page<GroupingByPsDto> findMovementsGroupByHealthCareProfessional(String search,
                        String sort, int page, int size) {

                String[] sortParts = Optional.ofNullable(sort).filter(s -> s.contains(","))
                                .map(s -> s.split(","))
                                .orElse(new String[] { "healthcareProfessionalName", "ASC" });
                Page<Object[]> result = movementRepository
                                .findMovementsGroupByHealthCareProfessional(search, sortParts[0],
                                                sortParts.length > 1 ? sortParts[1] : "asc",
                                                PageRequest.of(page, size));

                return result.map(obj -> new GroupingByPsDto((String) obj[0], (String) obj[1],
                                (String) obj[2], (Long) obj[3],
                                ((BigDecimal) obj[4]).doubleValue()));

        }

        public Page<GroupingByAdherantDto> findMovmentsGroupByAdherant(String search, String sort,
                        int page, int size) {
                String[] sortParts = Optional.ofNullable(sort).filter(s -> s.contains(","))
                                .map(s -> s.split(","))
                                .orElse(new String[] { "adherantName", "asc" });
                System.out.println("sortParts[0] = " + sortParts[0]);
                System.out.println("sortParts[1] = " + sortParts[1]);

                Page<Object[]> result = movementRepository.findMovementsGroupByAdherant(search,
                                sortParts[0], sortParts[1], PageRequest.of(page, size));

                return result.map(obj -> new GroupingByAdherantDto((String) obj[0], (Long) obj[1],
                                (String) obj[2], (Long) obj[3],
                                ((BigDecimal) obj[4]).doubleValue()));
        }

        public Page<GroupingByDateDto> findMovemntGroupByDate(String period, String search,
                        String sort, int page, int size) {
                String[] sortParts = Optional.ofNullable(sort).filter(s -> s.contains(","))
                                .map(s -> s.split(",")).orElse(new String[] { "datePeriod", "asc" });

                Page<Object[]> result = movementRepository.findMovementsGroupByDate(period, search,
                                sortParts[0], sortParts.length > 1 ? sortParts[1] : "asc",
                                PageRequest.of(page, size));

                return result.map(obj -> new GroupingByDateDto((String) obj[0], (Long) obj[1],
                                ((BigDecimal) obj[2]).doubleValue()));

        }

        public List<MovementResponseDTO> getAllMovementsWithDetails() {
                List<Movement> movements = movementRepository.findAll();

                Map<String, List<Movement>> movementsBySpecialty = movements.stream()
                                .collect(Collectors.groupingBy(m -> m.getHealthcareProfessional()
                                                .getmedicalSpecialty()));

                Map<String, BigDecimal> specialtyAverages = movementsBySpecialty.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                                        List<Movement> specialtyMovements = entry.getValue();
                                        BigDecimal total = specialtyMovements.stream()
                                                        .map(Movement::getAmount)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                                        return total.divide(
                                                        BigDecimal.valueOf(
                                                                        specialtyMovements.size()),
                                                        2, BigDecimal.ROUND_HALF_UP);
                                }));

                // Convert to DTOs
                List<MovementResponseDTO> responseList = new ArrayList<>();

                for (Movement movement : movements) {
                        MovementResponseDTO dto = new MovementResponseDTO();
                        dto.setMovementId(movement.getId());
                        dto.setDoctorId(movement.getHealthcareProfessional().getId());
                        dto.setDoctorName(movement.getHealthcareProfessional().getName());
                        dto.setMedicalSpecialty(
                                        movement.getHealthcareProfessional().getmedicalSpecialty());
                        dto.setAdherantId(movement.getAdherant().getId());
                        dto.setAdherantName(movement.getAdherant().getFirstName() + " "
                                        + movement.getAdherant().getLastName());
                        dto.setAdherantDeductible(movement.getAdherant().getDeductible());
                        dto.setAmount(movement.getAmount());
                        dto.setDate(movement.getDate());
                        dto.setDescription(movement.getDescription());
                        dto.setSpecialtyAverageAmount(specialtyAverages.get(movement
                                        .getHealthcareProfessional().getmedicalSpecialty()));

                        responseList.add(dto);
                }

                return responseList;
        }

        public Page<Movement> getMovements(int page, int size, String search, String sort) {
                String[] sortParts = Optional.ofNullable(sort).filter(s -> s.contains(","))
                                .map(s -> s.split(",")).orElse(new String[] { "datePeriod", "asc" });
                sortParts[0] = sortParts[0].equals("datePeriod") ? "date" : sortParts[0];
                sortParts[1] = sortParts.length > 1 ? sortParts[1] : "asc";
                return movementRepository.findMovementsWithSearchAndSort(PageRequest.of(page, size),
                                search, sortParts[0], sortParts[1]);

        }

       

}
