
package com.example.Annuaire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
@EntityScan("com.example.Annuaire.Models")
@EnableJpaRepositories("com.example.Annuaire.Repository")
@ComponentScan(basePackages = { "com.example.Annuaire" })
public class AnnuaireApplication {

	public static void main(String[] args) {
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("sun.jnu.encoding", "UTF-8");	
		SpringApplication.run(AnnuaireApplication.class, args);
	}

	static {
		// Set encoding before application starts
		System.setProperty("file.encoding", "UTF-8");
		System.setProperty("sun.jnu.encoding", "UTF-8");
	}

	@PostConstruct
	void started() {
		// Ensure UTF-8 is set after initialization
		System.setProperty("file.encoding", "UTF-8");
	}

}
