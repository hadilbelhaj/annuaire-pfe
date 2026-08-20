package com.example.Annuaire.Controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Annuaire.Models.Adherant;
import com.example.Annuaire.Models.Contrat;
import com.example.Annuaire.Repository.AdherantRepository;
import com.example.Annuaire.Repository.ContratRepository;

import jakarta.transaction.Transactional;



@RestController
@RequestMapping("/api/adherant")
public class AdhearntController {
    @Autowired
    private final AdherantRepository adherantRepository;

    private final ContratRepository contratRepository;

    public AdhearntController(AdherantRepository adherantRepository,
            ContratRepository contratRepository) {
        this.adherantRepository = adherantRepository;
        this.contratRepository = contratRepository;
    }

    @PostMapping("/update-regions")
    public ResponseEntity<String> updateAllAdherantsWithRandomRegions() {

        List<Adherant> adherants = adherantRepository.findAll();
        for (Adherant adherant : adherants) {
            adherant.setRegion(generateRandomTunisianRegion());
        }
        adherantRepository.saveAll(adherants);

        return ResponseEntity.ok("Successfully updated " + adherants.size()
                + " adherants with random Tunisian regions");
    }

    private String generateRandomTunisianRegion() {
        String[] tunisianRegions = {"Ariana", "Sousse", "Sfax", "Monastir", "Tunis", "Ben Arous",
                "Kébili", "Bizerte", "Mahdia", "Tataouine", "Kairouan", "Sidi Bouzid", "Siliana",
                "Jendouba", "Nabeul", "Gafsa", "Zaghouan", "Tozeur", "Manouba", "Kasserine",
                "La Manouba", "Medenine", "Le Kef", "Gabes", "Beja"};

        int randomIndex = (int) (Math.random() * tunisianRegions.length);
        return tunisianRegions[randomIndex];
    }

    @GetMapping
    public List<Adherant> getAllAdherants() {
        return adherantRepository.findAll();
    }

    @Transactional
    @Modifying
    @PostMapping("/associatecontracts")
    public String associatecontracts() {
        List<Adherant> adherants = adherantRepository.findAll();
        long nbcontracts = contratRepository.count();

        long counter = 1;
        for (Adherant adherant : adherants) {
            Optional<Contrat> c = contratRepository.findById(counter);
            if (c.isPresent()) {
                System.out.println(counter);

                adherantRepository.setContratId(adherant.getId(), counter);


            } else {
                System.out.println("contract for  counter :  " + counter + "not found");
            }

            counter++;
            if (nbcontracts < counter) {
                break;
            }
        }
        return "ok";
    }

    @Transactional
    @Modifying
    @PostMapping("/changedeductible")
    public String changeDeductible() {
        int s = 1000;
        int g = 3000;
        int p = 10000;
        List<Adherant> adherants = adherantRepository.findAll();
        for (Adherant adherant : adherants) {
            if (adherant.getContract() == null) {
                continue;
            } else {
                if (adherant.getContract().getFormule().getLibelle_formule()
                        .equals("silver  plan")) {
                    adherantRepository.setDeductible(s, adherant.getId());
                } else if (adherant.getContract().getFormule().getLibelle_formule()
                        .equals("gold plan")) {
                    adherantRepository.setDeductible(g, adherant.getId());
                } else {
                    adherantRepository.setDeductible(p, adherant.getId());
                }
            }
        }
        return "ok";
    }


    @GetMapping("/contrats")
    public ResponseEntity<List<Contrat>> getContracts() {
        return ResponseEntity.ok(contratRepository.findAll());
    }


}
