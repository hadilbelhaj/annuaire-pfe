package com.example.Annuaire.Service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

import com.example.Annuaire.Exceptions.ResourceNotFoundException;
import com.example.Annuaire.Models.Adherant;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Repository.AdherantRepository;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Service.Localisation.GeocodingService;
import com.example.Annuaire.Specification.HealthcareProfessionalSpecification;

import jakarta.persistence.EntityNotFoundException;

@Service
public class HealthcareProfessionalService {
    private final HealthcareProfessionalRepository repository;
    @Autowired
    private AdherantRepository adherentRepository;
    @Autowired
    private GeocodingService geocodingService;

    public Long findHealthcareProfessionalIdByName(String name) {
        HealthcareProfessional professional = repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Healthcare professional not found with name: " + name));
        return professional.getId();
    }

    public Long findAdherentIdByName(String firstName, String lastName) {
        Adherant adherent = adherentRepository.findByFirstNameAndLastName(firstName, lastName)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Adherent not found with name: " + firstName + " " + lastName));
        return adherent.getId();
    }

    public HealthcareProfessionalService(HealthcareProfessionalRepository repository,
            GeocodingService geocodingService) {
        this.repository = repository;
        this.geocodingService = geocodingService;
    }

    public Page<HealthcareProfessional> getPaginatedProfessionals(int page, int size, boolean authenticated,
            String searchTerm, String deletedFilter) {
        PageRequest pageable = PageRequest.of(page, size);

        Specification<HealthcareProfessional> baseSpec;
        if (authenticated) {
            baseSpec = Specification.where(null);
        } else {
            baseSpec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("conventionne"), 0);
        }

        if (deletedFilter != null) {
            Specification<HealthcareProfessional> deletedSpec;
            switch (deletedFilter) {
                case "deleted-only":
                    deletedSpec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), 1);
                    baseSpec = baseSpec.and(deletedSpec);
                    break;
                case "active-only":
                    deletedSpec = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("deleted"), 0);
                    baseSpec = baseSpec.and(deletedSpec);
                    break;
                case "all":
                default:

                    break;
            }
        } else {

            Specification<HealthcareProfessional> defaultActiveSpec = (root, query, criteriaBuilder) -> criteriaBuilder
                    .equal(root.get("deleted"), 0);
            baseSpec = baseSpec.and(defaultActiveSpec);
        }

        if (authenticated && searchTerm != null && !searchTerm.isEmpty()) {
            Specification<HealthcareProfessional> searchSpec = HealthcareProfessionalSpecification.searchBy(searchTerm);
            return repository.findAll(baseSpec.and(searchSpec), pageable);
        } else {
            return repository.findAll(baseSpec, pageable);
        }
    }

    public int getTotalPages(int size) {
        return (int) Math.ceil((double) repository.count() / size);
    }

    public Page<HealthcareProfessional> getPaginatedProfessionalsBySearch(int page, int size, String name,
            boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionals(name, pageable);
        } else {
            return repository.searchProfessionalsByconventionne(name, 0, pageable);
        }
    }

    public List<HealthcareProfessional> getProfessionalsBySearch(String search, boolean authenticated) {
        List<HealthcareProfessional> professionals = repository.findAll();
        String searchQuery = (search == null) ? "" : search.toLowerCase();

        return professionals.stream()
                .filter(professional -> (authenticated || professional.getConventionne() == 0) &&
                        (searchQuery.isEmpty() || professional.getName().toLowerCase().contains(searchQuery)))
                .collect(Collectors.toList());
    }

    public Page<HealthcareProfessional> getPaginatedProfessionalsByRegion(int page, int size, String region,
            boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionalsByRegion(region, pageable);
        } else {
            return repository.searchProfessionalsByRegionAndconventionne(region, 0, pageable);
        }
    }

    public Page<HealthcareProfessional> getPaginatedProfessionalsBySpecialty(int page, int size, String specialty,
            boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionalsBySpecialty(specialty, pageable);
        } else {
            return repository.searchProfessionalsBySpecialtyAndconventionne(specialty, 0, pageable);
        }
    }

    public Page<HealthcareProfessional> getPaginatedProfessionalsBySpecialtyRegionName(
            int page, int size, String specialty, String region, String name, boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionalsBySpecialtyRegionName(region, specialty, name, pageable);
        } else {
            return repository.searchProfessionalsBySpecialtyRegionNameAndconventionne(region, specialty, name, 0,
                    pageable);
        }
    }

    public Page<HealthcareProfessional> getPageOfProfessionalsBySpecialtyRegion(
            int page, int size, String specialty, String region, boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionalsBySpecialtyRegion(specialty, region, pageable);
        } else {
            return repository.searchProfessionalsBySpecialtyRegionAndconventionne(specialty, region, 0, pageable);
        }
    }

    public Page<HealthcareProfessional> getPageOfProfessionalsByNameRegion(
            int page, int size, String name, String region, boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionalsByNameRegion(region, name, pageable);
        } else {
            return repository.searchProfessionalsByNameRegionAndconventionne(region, name, 0, pageable);
        }
    }

    public Page<HealthcareProfessional> getPageOfProfessionalsByNameSpecialty(
            int page, int size, String name, String specialty, boolean authenticated) {
        PageRequest pageable = PageRequest.of(page, size);
        if (authenticated) {
            return repository.searchProfessionalsByNameSpecialty(specialty, name, pageable);
        } else {
            return repository.searchProfessionalsByNameSpecialtyAndconventionne(specialty, name, 0, pageable);
        }
    }

    public List<HealthcareProfessional> getAllHealthcareProfessionals() {
        return repository.findAllActive();
    }

    public HealthcareProfessional getHealthcareProfessionalById(Long id) {
        return repository.findByIdIfNotDeleted(id)
                .orElseThrow(() -> new ResourceNotFoundException("Healthcare Professional not found with id: " + id));
    }

    public HealthcareProfessional createHealthcareProfessional(HealthcareProfessional healthcareProfessional) {
        healthcareProfessional.setDeleted(0);
        return repository.save(healthcareProfessional);
    }

    public HealthcareProfessional updateHealthcareProfessional(Long id,
            HealthcareProfessional updatedHealthcareProfessional) {
        HealthcareProfessional existingHealthcareProfessional = getHealthcareProfessionalById(id);

        existingHealthcareProfessional.setName(updatedHealthcareProfessional.getName());
        existingHealthcareProfessional.setmedicalSpecialty(updatedHealthcareProfessional.getmedicalSpecialty());
        existingHealthcareProfessional.setNumber1(updatedHealthcareProfessional.getNumber1());
        existingHealthcareProfessional.setNumber2(updatedHealthcareProfessional.getNumber2());
        existingHealthcareProfessional.setMail(updatedHealthcareProfessional.getMail());
        existingHealthcareProfessional.setAddress(updatedHealthcareProfessional.getAddress());
        existingHealthcareProfessional.setAdditionalAttributes(updatedHealthcareProfessional.getAdditionalAttributes());
        existingHealthcareProfessional.setNumeroOrdre(updatedHealthcareProfessional.getNumeroOrdre());
        existingHealthcareProfessional.setRegion(updatedHealthcareProfessional.getRegion());
        existingHealthcareProfessional.setConventionne(updatedHealthcareProfessional.getConventionne());
        existingHealthcareProfessional.setRef(updatedHealthcareProfessional.getRef());
        existingHealthcareProfessional.setNumFiscal(updatedHealthcareProfessional.getNumFiscal());
        existingHealthcareProfessional
                .setAdditionalAttributes(updatedHealthcareProfessional.getAdditionalAttributes());

        existingHealthcareProfessional.setDeleted(0);

        return repository.save(existingHealthcareProfessional);
    }

    public void deleteHealthcareProfessional(Long id) {
        HealthcareProfessional healthcareProfessional = getHealthcareProfessionalById(id);
        healthcareProfessional.setDeleted(1);
        repository.save(healthcareProfessional);
    }

    public HealthcareProfessional restoreHealthcareProfessional(Long id) {
        HealthcareProfessional healthcareProfessional = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Healthcare Professional not found with id: " + id));
        healthcareProfessional.setDeleted(0);
        return repository.save(healthcareProfessional);
    }

    public List<HealthcareProfessional> getAllHealthcareProfessionalsIncludingDeleted() {
        return repository.findAll();
    }

    public void updateAllCoordinates() {
        List<HealthcareProfessional> professionals = repository.findAll();
        for (HealthcareProfessional hp : professionals) {
            if (hp.getLatitude() == null || hp.getLongitude() == null) {
                double[] coords = geocodingService.geocodeAddress(hp.getAddress());
                if (coords != null) {
                    hp.setLatitude(coords[0]);
                    hp.setLongitude(coords[1]);
                    repository.save(hp);
                }
            }
        }
    }
}