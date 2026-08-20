package com.example.Annuaire.Service.Localisation;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Service.HealthcareProfessionalService;
import com.example.Annuaire.enums.Enums;
import com.example.DTOS.prestations.NearbyProviderDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.Objects;

@Service
public class ProximitySearchService {
        private final HealthcareProfessionalRepository hcpRepository;
        private static final Logger logger = LoggerFactory.getLogger(ProximitySearchService.class);

        @Autowired
        public ProximitySearchService(
                        HealthcareProfessionalRepository hcpRepository) {
                this.hcpRepository = hcpRepository;

        }

        public Page<NearbyProviderDTO> findNearbyProvidersWithSpatial(
                        Double latitude,
                        Double longitude,
                        Enums.Prestation_libelle prestationLibelle,
                        Double maxDistanceKm,
                        boolean authenticated,
                        Pageable pageable) {

                double maxDistanceMeters = maxDistanceKm * 1000;

                Page<Object[]> resultPage;
                if (prestationLibelle != null) {
                        logger.info("Searching for professionals with prestation: {}", prestationLibelle);
                        resultPage = hcpRepository.findNearbyProfessionalsByPrestation(
                                        latitude, longitude, prestationLibelle.name(), maxDistanceMeters, pageable);
                } else {
                        resultPage = hcpRepository.findNearbyProfessionals(
                                        latitude, longitude, maxDistanceMeters, pageable);
                }

                List<NearbyProviderDTO> dtos = resultPage.getContent().stream()
                                .map(result -> {
                                        Long id = (result[0] == null || (result[0] instanceof String
                                                        && ((String) result[0]).isEmpty()))
                                                                        ? -1L
                                                                        : (result[0] instanceof Number
                                                                                        ? ((Number) result[0])
                                                                                                        .longValue()
                                                                                        : Long.parseLong(
                                                                                                        (String) result[0]));

                                        String name = (String) result[1];
                                        String address = (String) result[2];

                                        // For latitude, use a default or null if empty
                                        Double lat = (result[3] == null || (result[3] instanceof String
                                                        && ((String) result[3]).isEmpty()))
                                                                        ? null
                                                                        : (result[3] instanceof Number
                                                                                        ? ((Number) result[3])
                                                                                                        .doubleValue()
                                                                                        : Double.parseDouble(
                                                                                                        (String) result[3]));

                                        // For longitude, use a default or null if empty
                                        Double lng = (result[4] == null || (result[4] instanceof String
                                                        && ((String) result[4]).isEmpty()))
                                                                        ? null
                                                                        : (result[4] instanceof Number
                                                                                        ? ((Number) result[4])
                                                                                                        .doubleValue()
                                                                                        : Double.parseDouble(
                                                                                                        (String) result[4]));

                                        // For distance, use a default value like 0.0 if empty
                                        Double distance = (result[result.length - 1] == null ||
                                                        (result[result.length - 1] instanceof String &&
                                                                        ((String) result[result.length - 1]).isEmpty()))
                                                                                        ? 0.0
                                                                                        : (result[result.length
                                                                                                        - 1] instanceof Number
                                                                                                                        ? ((Number) result[result.length
                                                                                                                                        - 1])
                                                                                                                                        .doubleValue()
                                                                                                                                        / 1000
                                                                                                                        : Double.parseDouble(
                                                                                                                                        (String) result[result.length
                                                                                                                                                        - 1])
                                                                                                                                        / 1000);

                                        NearbyProviderDTO dto = new NearbyProviderDTO();
                                        dto.setId(id);
                                        dto.setName(name);
                                        dto.setAddress(address);
                                        dto.setLatitude(lat);
                                        dto.setLongitude(lng);
                                        dto.setDistanceKm(distance);
                                        return dto;
                                })
                                .filter(dto -> dto.getLatitude() != null && dto.getLongitude() != null)
                                .collect(Collectors.toList());

                return new PageImpl<>(dtos, pageable, resultPage.getTotalElements());
        }
}
