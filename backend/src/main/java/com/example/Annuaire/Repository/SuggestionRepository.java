package com.example.Annuaire.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.Annuaire.Models.Suggestion;
import com.example.Annuaire.Models.User;

import java.util.List;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    List<Suggestion> findByUser(User user);
    List<Suggestion> findByStatusOrderByUpvotesDesc(Suggestion.SuggestionStatus status);
    List<Suggestion> findAllByOrderByCreatedAtDesc();
    List<Suggestion> findByCategoryOrderByCreatedAtDesc(String category);
}
