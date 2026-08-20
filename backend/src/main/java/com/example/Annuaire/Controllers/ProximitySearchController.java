package com.example.Annuaire.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.User;
import com.example.Annuaire.Service.UserService;
import com.example.Annuaire.Service.Localisation.GeocodingService;
import com.example.Annuaire.Service.Localisation.ProximitySearchService;
import com.example.Annuaire.enums.Enums;
import com.example.DTOS.prestations.NearbyProviderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/proximity")
public class ProximitySearchController {
    private final ProximitySearchService proximitySearchService;
    private final GeocodingService geocodingService;
    private final UserService userService;

    @Autowired
    public ProximitySearchController(ProximitySearchService proximitySearchService, GeocodingService geocodingService,
            UserService userService) {
        this.proximitySearchService = proximitySearchService;
        this.geocodingService = geocodingService;
        this.userService = userService;
    }

    @GetMapping("/search")
    public ResponseEntity<Page<NearbyProviderDTO>> findNearbyProviders(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(required = false) Enums.Prestation_libelle prestation,
            @RequestParam(required = false, defaultValue = "10.0") Double maxDistanceKm,
            @RequestParam(required = false, defaultValue = "false") boolean authenticated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<NearbyProviderDTO> providers = proximitySearchService.findNearbyProvidersWithSpatial(
                latitude, longitude, prestation, maxDistanceKm, authenticated, pageable);

        return ResponseEntity.ok(providers);
    }

    @GetMapping("/search/user")
    public ResponseEntity<Page<NearbyProviderDTO>> findNearbyProvidersForUser(
            @RequestParam(required = false) Enums.Prestation_libelle prestation,
            @RequestParam(required = false, defaultValue = "10.0") Double maxDistanceKm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        System.out.println("Authentication object: " + authentication);

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        User user = userService.getCurrentUser(authentication);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        if (user.getLatitude() == null || user.getLongitude() == null) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<NearbyProviderDTO> providers = proximitySearchService.findNearbyProvidersWithSpatial(
                user.getLatitude(), user.getLongitude(), prestation, maxDistanceKm, true, pageable);

        return ResponseEntity.ok(providers);
    }

    @GetMapping("/search/address")
    public ResponseEntity<Page<NearbyProviderDTO>> findNearbyProvidersByAddress(
            @RequestParam String address,
            @RequestParam(required = false) Enums.Prestation_libelle prestation,
            @RequestParam(required = false, defaultValue = "10.0") Double maxDistanceKm,
            @RequestParam(required = false, defaultValue = "false") boolean authenticated,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        double[] coordinates = geocodingService.geocodeAddress(address);

        if (coordinates == null) {
            return ResponseEntity.badRequest()
                    .body(null);
        }

        double latitude = coordinates[0];
        double longitude = coordinates[1];
        System.out.println("latitude:" + latitude + "longitude" + longitude);

        Pageable pageable = PageRequest.of(page, size);

        Page<NearbyProviderDTO> providers = proximitySearchService.findNearbyProvidersWithSpatial(
                latitude, longitude, prestation, maxDistanceKm, authenticated, pageable);

        return ResponseEntity.ok(providers);
    }

}
