package com.example.Annuaire.Controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.ActePS;
import com.example.Annuaire.Models.HealthcareProfessional;
import com.example.Annuaire.Models.HealthcareProfessionalRequest;
import com.example.Annuaire.Models.Prestation;
import com.example.Annuaire.Repository.HealthcareProfessionalRepository;
import com.example.Annuaire.Service.ActePSService;
import com.example.Annuaire.Service.HealthcareProfessionalService;
import com.example.Annuaire.Service.Localisation.GeocodingService;
import com.example.Annuaire.Utils.TaxNumberGenerator;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.Coordinate;

import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/ps")
@CrossOrigin(origins = "*")
public class HealthcareProfessionalController {
    @Autowired
    private final HealthcareProfessionalRepository repository;
    @Autowired
    private final HealthcareProfessionalService service;
    @Autowired
    private GeocodingService geocodingService;
    private final ActePSService actePSService;

    public HealthcareProfessionalController(HealthcareProfessionalRepository repository,
            HealthcareProfessionalService service, ActePSService actePSService, GeocodingService geocodingService) {
        this.repository = repository;
        this.service = service;
        this.actePSService = actePSService;
        this.geocodingService = geocodingService;
    }

    @PutMapping("/all")
    public ResponseEntity<?> geocodeAll() {
        service.updateAllCoordinates();
        return ResponseEntity.ok("All professionals geocoded (if needed).");
    }

    @GetMapping
    public ResponseEntity<List<HealthcareProfessional>> getAllProfessionals() {
        List<HealthcareProfessional> professionals = repository.findAll();
        return new ResponseEntity<>(professionals, HttpStatus.OK);
    }

    @GetMapping("/specs")
    public ResponseEntity<List<String>> getDistinctSpecialities() {
        List<String> specialities = repository.findDistinctMedicalSpeciality();
        return new ResponseEntity<>(specialities, HttpStatus.OK);
    }

    @GetMapping("/regions")
    public ResponseEntity<List<String>> getDistinctRegions() {
        List<String> regions = repository.findDistinctRegion();
        return new ResponseEntity<>(regions, HttpStatus.OK);
    }

    @GetMapping("/totalPages/{size}")
    public ResponseEntity<Integer> getTotalPages(@PathVariable("size") String sizeString) {
        Integer size = Integer.parseInt(sizeString);
        Integer totalPages = service.getTotalPages(size);
        return new ResponseEntity<>(totalPages, HttpStatus.OK);
    }

    @GetMapping("/paginator/{page}/{size}")
    public ResponseEntity<Page<HealthcareProfessional>> getProfessionalsByPage(
            @PathVariable("page") String pagesString,
            @PathVariable("size") String sizeString,
            @RequestParam(defaultValue = "false") boolean authenticated,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "all") String deletedFilter) {
        Integer size = Integer.parseInt(sizeString);
        Integer page = Integer.parseInt(pagesString);

        Page<HealthcareProfessional> professionals = service.getPaginatedProfessionals(page, size, authenticated,
                search, deletedFilter);
        return new ResponseEntity<>(professionals, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<HealthcareProfessional>> getProfessionalsBySearch(
            @RequestParam("search") String search,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        List<HealthcareProfessional> professionals = service.getProfessionalsBySearch(search, authenticated);
        return new ResponseEntity<>(professionals, HttpStatus.OK);
    }

    @GetMapping("/paginator/search/{page}/{size}")
    public ResponseEntity<Page<HealthcareProfessional>> getProfessionalsByPageAndSearch(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        Page<HealthcareProfessional> professionals = service.getPaginatedProfessionalsBySearch(page, size, name,
                authenticated);
        return ResponseEntity.ok(professionals);
    }

    @GetMapping("/paginator/speciality/{page}/{size}")
    public ResponseEntity<Page<HealthcareProfessional>> getProfessionalsByPageAndSpecialty(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String speciality,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        return ResponseEntity.ok(service.getPaginatedProfessionalsBySpecialty(page, size, speciality, authenticated));
    }

    @GetMapping("/paginator/region/{page}/{size}")
    public ResponseEntity<Page<HealthcareProfessional>> getProfessionalsByPageAndRegion(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String region,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        return ResponseEntity.ok(service.getPaginatedProfessionalsByRegion(page, size, region, authenticated));
    }

    @GetMapping("/paginator/specialty/region/{page}/{size}")
    public ResponseEntity<Page<HealthcareProfessional>> getProfessionalsByAll(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String speciality,
            @RequestParam String region,
            @RequestParam String name,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        return ResponseEntity.ok(service.getPaginatedProfessionalsBySpecialtyRegionName(page, size, speciality, region,
                name, authenticated));
    }

    @GetMapping("/page/specialty/region/{page}/{size}")
    public Page<HealthcareProfessional> getProfessionalsBySpecialtyRegion(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String speciality,
            @RequestParam String region,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        return service.getPageOfProfessionalsBySpecialtyRegion(page, size, speciality, region, authenticated);
    }

    @GetMapping("/page/name/region/{page}/{size}")
    public Page<HealthcareProfessional> getPageOfProfessionalsByNameRegion(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String name,
            @RequestParam String region,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        return service.getPageOfProfessionalsByNameRegion(page, size, name, region, authenticated);
    }

    @GetMapping("/page/name/specialty/{page}/{size}")
    public Page<HealthcareProfessional> getPageOfProfessionalsByNameSpecialty(
            @PathVariable int page,
            @PathVariable int size,
            @RequestParam String name,
            @RequestParam String specialty,
            @RequestParam(defaultValue = "false") boolean authenticated) {
        return service.getPageOfProfessionalsByNameSpecialty(page, size, name, specialty, authenticated);
    }

    @PostMapping("/setconv")
    public ResponseEntity<?> setConvtionne() {
        List<HealthcareProfessional> list_conv = new ArrayList<>();
        List<String> specialtieList = repository.findDistinctMedicalSpeciality();
        int size = 10;
        int page = 1;
        for (String speciality : specialtieList) {
            for (int j = 0; j <= page; j++) {
                List<HealthcareProfessional> L = (service.getPaginatedProfessionalsBySpecialty(page, size, speciality,
                        false))
                        .getContent();
                for (HealthcareProfessional i : L) {
                    Long id = i.getId();

                    Optional<HealthcareProfessional> ps = repository.findById(id);

                    if (ps.isPresent()) {
                        ps.get().setConventionne(1);
                        repository.save(ps.get());
                    } else {
                        System.out.println("non");
                    }
                    list_conv.add(i);
                }
            }
        }
        return ResponseEntity.status(200).body(list_conv.size());
    }

    @Transactional
    @PostMapping("/setref")
    public ResponseEntity<?> setPsRefs() {
        try {
            long totalCount = repository.countAllById();

            if (totalCount == 0) {

                return ResponseEntity.ok().body("No records to update");
            }
            long tpEnd = (long) (totalCount * 50 / 100);
            long lpEnd = tpEnd + (long) (totalCount * 45 / 100);
            int batchSize = 1000;
            int page = 0;
            Page<HealthcareProfessional> professionalPage;

            do {
                PageRequest pageable = PageRequest.of(page, batchSize);
                professionalPage = repository.findAll(pageable);

                for (HealthcareProfessional ps : professionalPage.getContent()) {
                    long id = ps.getId();
                    String refType = determineRefType(id, tpEnd, lpEnd);
                    String newRef = ps.getNumeroOrdre() + refType + "2025";

                    repository.updateRef(id, newRef);
                }
                repository.flush();
                System.out.println("Page " + page + " done");
                page++;

            } while (professionalPage.hasNext());

            return ResponseEntity.ok().body("Refs updated successfully");

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error updating refs");
        }
    }

    private String determineRefType(long id, long tpEnd, long lpEnd) {
        if (id <= tpEnd) {
            return "TP";
        } else if (id <= lpEnd) {
            return "LP";
        }
        return "PEK";
    }

    @Transactional
    @Modifying
    @PostMapping("/setnumfiscal")
    public ResponseEntity<?> setNumFiscal() {
        for (HealthcareProfessional ps : repository.findAll()) {
            long id = ps.getId();
            String numFiscal = TaxNumberGenerator.generateValidTaxNumber();
            repository.updateNumFiscal(id, numFiscal);
            System.out.println("Num fiscal updated for " + id);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping(consumes = { "application/json", "application/json;charset=UTF-8" })
    public ResponseEntity<HealthcareProfessional> createHealthcareProfessional(
            @RequestBody HealthcareProfessionalRequest request) {
        HealthcareProfessional healthcareProfessional = request.getHealthcareProfessional();

        // Set default location for geocoding failures
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326); // 4326 is SRID for WGS 84

        if (healthcareProfessional.getAddress() != null && !healthcareProfessional.getAddress().isEmpty()) {
            try {
                double[] coords = geocodingService.geocodeAddress(healthcareProfessional.getAddress());
                if (coords != null) {
                    // Set latitude and longitude fields
                    Double lat = coords[0];
                    Double lng = coords[1];
                    healthcareProfessional.setLatitude(lat);
                    healthcareProfessional.setLongitude(lng);

                    // Create a Point object for the location field (note: lon=x, lat=y in spatial
                    // coordinates)
                    Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
                    healthcareProfessional.setLocation(point);
                } else {
                    // Set default location with 0,0 coordinates
                    healthcareProfessional.setLatitude(0.0);
                    healthcareProfessional.setLongitude(0.0);
                    Point defaultPoint = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
                    healthcareProfessional.setLocation(defaultPoint);
                }
            } catch (Exception e) {
                // On any geocoding error, set default location
                healthcareProfessional.setLatitude(0.0);
                healthcareProfessional.setLongitude(0.0);
                Point defaultPoint = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
                healthcareProfessional.setLocation(defaultPoint);
            }
        } else {
            // Even without an address, set default location
            healthcareProfessional.setLatitude(0.0);
            healthcareProfessional.setLongitude(0.0);
            Point defaultPoint = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
            healthcareProfessional.setLocation(defaultPoint);
        }

        HealthcareProfessional createdProfessional = service.createHealthcareProfessional(healthcareProfessional);
        if (request.getPrestationLabels() != null && !request.getPrestationLabels().isEmpty()) {
            actePSService.associatePrestationsToPS(createdProfessional.getId(), request.getPrestationLabels());
        }

        return new ResponseEntity<>(createdProfessional, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthcareProfessional> updateHealthcareProfessional(
            @PathVariable Long id,
            @RequestBody HealthcareProfessionalRequest request) {

        HealthcareProfessional healthcareProfessional = request.getHealthcareProfessional();

        if (healthcareProfessional.getAddress() != null && !healthcareProfessional.getAddress().isEmpty()) {
            try {
                double[] coords = geocodingService.geocodeAddress(healthcareProfessional.getAddress());
                if (coords != null) {

                    Double lat = coords[0];
                    Double lng = coords[1];
                    healthcareProfessional.setLatitude(lat);
                    healthcareProfessional.setLongitude(lng);

                    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326); // 4326 is the
                                                                                                       // SRID for WGS
                                                                                                       // 84
                    Point point = geometryFactory.createPoint(new Coordinate(lng, lat));
                    healthcareProfessional.setLocation(point);
                } else {

                    healthcareProfessional.setLatitude(0.0);
                    healthcareProfessional.setLongitude(0.0);
                    GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                    Point defaultPoint = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
                    healthcareProfessional.setLocation(defaultPoint);
                }
            } catch (Exception e) {

                healthcareProfessional.setLatitude(0.0);
                healthcareProfessional.setLongitude(0.0);
                GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
                Point defaultPoint = geometryFactory.createPoint(new Coordinate(0.0, 0.0));
                healthcareProfessional.setLocation(defaultPoint);
            }
        }

        HealthcareProfessional updatedProfessional = service.updateHealthcareProfessional(id, healthcareProfessional);

        if (request.getPrestationLabels() != null && !request.getPrestationLabels().isEmpty()) {
            actePSService.associatePrestationsToPS(updatedProfessional.getId(), request.getPrestationLabels());
        }

        return ResponseEntity.ok(updatedProfessional);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthcareProfessional(@PathVariable Long id) {
        service.deleteHealthcareProfessional(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/admin/restore/{id}")
    public ResponseEntity<HealthcareProfessional> restoreHealthcareProfessional(@PathVariable Long id) {
        HealthcareProfessional restoredProfessional = service.restoreHealthcareProfessional(id);
        return ResponseEntity.ok(restoredProfessional);
    }

    @GetMapping("/prestations/{id}")
    public ResponseEntity<List<Prestation>> getPrestationsByHealthcareProfessionalId(@PathVariable Long id) {
        List<ActePS> actes = actePSService.getPrestationsByHealthcareProfessionalId(id);

        List<Prestation> prestations = actes.stream()
                .map(ActePS::getPrestation)
                .distinct()
                .collect(Collectors.toList());

        return ResponseEntity.ok(prestations);
    }
    
}
