package com.example.DTOS.prestations;

import lombok.Data;

@Data
public class NearbyProviderDTO {
    private Long id;
    private String name;
    private String medicalSpecialty;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;
    private String contact;

    public void setLatitude(Double latitude) {
        this.latitude = (latitude != null) ? latitude : 0.0;
    }

    public void setLongitude(Double longitude) {
        this.longitude = (longitude != null) ? longitude : 0.0;
    }
}
