package com.example.Annuaire.Models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.locationtech.jts.geom.Point;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" }) // Add this line
@Entity
@Getter
@Setter
@Table(name = "healthcare_professional")
public class HealthcareProfessional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "healthcare_professional_id")
    private Long id;

    private String name;
    private String medicalSpecialty;
    private String number1;
    private String number2;
    private String mail;
    @Column(columnDefinition = "TEXT")
    private String address;
    @Column(columnDefinition = "TEXT")
    private String additionalAttributes;
    @JsonProperty("NumeroOrdre")
    private String numeroOrdre;

    @OneToMany(mappedBy = "healthcareProfessional", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<AdditionalAttributesPs> additionalAttributes2;

    private String region;

    private int conventionne;
    private String ref;
    private String numFiscal;
    private int deleted = 0;
    private Double latitude;
    private Double longitude;
    @Column(columnDefinition = "POINT")
    @JsonIgnore
    private Point location;

    public HealthcareProfessional() {
    }

    public HealthcareProfessional(String nom, String specialite, String address, String number1,
            String number2, String mail, String NumeroOrdre, String region,
            String additionalAttributes) {
        this.name = nom;
        this.medicalSpecialty = specialite;
        this.address = address;
        this.number1 = number1;
        this.number2 = number2;
        this.mail = mail;
        this.region = region;
        this.additionalAttributes = additionalAttributes;
        this.numeroOrdre = NumeroOrdre;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nom) {
        this.name = nom;
    }

    public String getmedicalSpecialty() {
        return medicalSpecialty;
    }

    public void setmedicalSpecialty(String specialite) {
        this.medicalSpecialty = specialite;
    }

    public String getAdditionalAttributes() {
        return additionalAttributes;
    }

    public void setAdditionalAttributes(String additionalAttributes) {
        this.additionalAttributes = additionalAttributes;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number) {
        this.number1 = number;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number) {
        this.number2 = number;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNumeroOrdre() {
        return numeroOrdre;
    }

    public void setNumeroOrdre(String numeroOrdre) {
        this.numeroOrdre = numeroOrdre;
    }

    public int getConventionne() {
        return conventionne;
    }

    public void setConventionne(int conventionne) {
        this.conventionne = conventionne;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getNumFiscal() {
        return numFiscal;
    }

    public void setNumFiscal(String numFiscal) {
        this.numFiscal = numFiscal;
    }

}
