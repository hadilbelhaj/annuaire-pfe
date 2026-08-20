package com.example.Annuaire.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.Annuaire.Models.SuggestionDTO;
import com.example.Annuaire.Service.SuggestionService;

import java.util.List;

@RestController
@RequestMapping("/api/suggestions")
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PostMapping
    public ResponseEntity<SuggestionDTO> createSuggestion(@RequestBody SuggestionDTO suggestionDTO,
            Authentication authentication) {
        SuggestionDTO createdSuggestion = suggestionService.createSuggestion(suggestionDTO);
        return new ResponseEntity<>(createdSuggestion, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SuggestionDTO>> getAllSuggestions() {
        List<SuggestionDTO> suggestions = suggestionService.getAllSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<SuggestionDTO>> getSuggestionsByCategory(@PathVariable String category) {
        List<SuggestionDTO> suggestions = suggestionService.getSuggestionsByCategory(category);
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/my-suggestions")
    public ResponseEntity<List<SuggestionDTO>> getMySuggestions() {
        List<SuggestionDTO> suggestions = suggestionService.getMySuggestions();
        return ResponseEntity.ok(suggestions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SuggestionDTO> getSuggestionById(@PathVariable Long id) {
        SuggestionDTO suggestion = suggestionService.getSuggestionById(id);
        return ResponseEntity.ok(suggestion);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SuggestionDTO> updateSuggestion(
            @PathVariable Long id,
            @RequestBody SuggestionDTO suggestionDTO) {
        SuggestionDTO updatedSuggestion = suggestionService.updateSuggestion(id, suggestionDTO);
        return ResponseEntity.ok(updatedSuggestion);
    }

    @PostMapping("/{id}/upvote")
    public ResponseEntity<Void> upvoteSuggestion(@PathVariable Long id) {
        suggestionService.upvoteSuggestion(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSuggestion(@PathVariable Long id) {
        suggestionService.deleteSuggestion(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<SuggestionDTO> updateSuggestionStatus(
            @PathVariable Long id,
            @RequestBody SuggestionDTO suggestionDTO) {
        SuggestionDTO updatedSuggestion = suggestionService.updateSuggestion(id, suggestionDTO);
        return ResponseEntity.ok(updatedSuggestion);
    }
}
