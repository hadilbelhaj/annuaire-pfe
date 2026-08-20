
package com.example.Annuaire.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Entity
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Adherant {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    private Long deductible;
    private String region;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id")
    private Contrat contract;

    public Adherant(String firstName, String lastName, String email, Long deductible) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.deductible = deductible;
        this.region = generateRandomTunisianRegion();
    }

    public String getName() {
        return this.getFirstName() + this.getLastName();
    }

    private String generateRandomTunisianRegion() {
        String[] tunisianRegions = { "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan",
                "Bizerte", "Béja", "Jendouba", "Kef", "Siliana", "Sousse", "Monastir", "Mahdia",
                "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid", "Gabès", "Medenine", "Tataouine",
                "Gafsa", "Tozeur", "Kebili" };

        int randomIndex = (int) (Math.random() * tunisianRegions.length);
        return tunisianRegions[randomIndex];
    }

    public Adherant(String firstName, String lastName, String email, Long deductible,
            String region) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.deductible = deductible;
        this.region = region;
    }

}
