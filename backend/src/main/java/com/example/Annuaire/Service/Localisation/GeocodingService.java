package com.example.Annuaire.Service.Localisation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeocodingService {
    private final RestTemplate restTemplate = new RestTemplate();

    public double[] geocodeAddress(String address) {
        try {
            String url = "https://photon.komoot.io/api/?q=" + URLEncoder.encode(address, StandardCharsets.UTF_8);
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            List<Map<String, Object>> features = (List<Map<String, Object>>) response.getBody().get("features");

            if (features != null && !features.isEmpty()) {
                Map<String, Object> geometry = (Map<String, Object>) features.get(0).get("geometry");
                List<Double> coords = (List<Double>) geometry.get("coordinates");
                return new double[] { coords.get(1), coords.get(0) };
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
