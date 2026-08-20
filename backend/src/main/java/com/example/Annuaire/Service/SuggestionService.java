package com.example.Annuaire.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.Suggestion;
import com.example.Annuaire.Models.SuggestionDTO;
import com.example.Annuaire.Models.User;
import com.example.Annuaire.Repository.SuggestionRepository;
import com.example.Annuaire.Repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SuggestionService {

    @Autowired
    private SuggestionRepository suggestionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public SuggestionDTO createSuggestion(SuggestionDTO suggestionDTO, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser == null) {
            throw new SecurityException("Authentication required to create suggestions");
        }

        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(suggestionDTO.getTitle());
        suggestion.setDescription(suggestionDTO.getDescription());
        suggestion.setCategory(suggestionDTO.getCategory());
        suggestion.setUser(currentUser);
        suggestion.setCreatedAt(LocalDateTime.now());
        suggestion.setStatus(Suggestion.SuggestionStatus.PENDING);

        Suggestion savedSuggestion = suggestionRepository.save(suggestion);
        return convertToDTO(savedSuggestion);
    }

    // Overloaded method that uses security context (for backward compatibility)
    public SuggestionDTO createSuggestion(SuggestionDTO suggestionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return createSuggestion(suggestionDTO, authentication);
    }

    public List<SuggestionDTO> getAllSuggestions() {
        return suggestionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SuggestionDTO> getSuggestionsByCategory(String category) {
        return suggestionRepository.findByCategoryOrderByCreatedAtDesc(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<SuggestionDTO> getMySuggestions(Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser == null) {
            throw new SecurityException("Authentication required to view your suggestions");
        }

        return suggestionRepository.findByUser(currentUser).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Overloaded method for backward compatibility
    public List<SuggestionDTO> getMySuggestions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return getMySuggestions(authentication);
    }

    public SuggestionDTO getSuggestionById(Long id) {
        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));
        return convertToDTO(suggestion);
    }

    public SuggestionDTO updateSuggestion(Long id, SuggestionDTO suggestionDTO, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser == null) {
            throw new SecurityException("Authentication required to update suggestions");
        }

        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));
        suggestion.setTitle(suggestionDTO.getTitle());
        suggestion.setDescription(suggestionDTO.getDescription());
        suggestion.setCategory(suggestionDTO.getCategory());

        suggestion.setStatus(suggestionDTO.getStatus());
        suggestion.setAdminFeedback(suggestionDTO.getAdminFeedback());

        Suggestion updatedSuggestion = suggestionRepository.save(suggestion);
        return convertToDTO(updatedSuggestion);
    }

    // Overloaded method for backward compatibility
    public SuggestionDTO updateSuggestion(Long id, SuggestionDTO suggestionDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return updateSuggestion(id, suggestionDTO, authentication);
    }

    public void upvoteSuggestion(Long id, Authentication authentication) {
        // Consider whether you need authentication to upvote
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser == null) {
            throw new SecurityException("Authentication required to upvote suggestions");
        }

        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));

        suggestion.setUpvotes(suggestion.getUpvotes() + 1);
        suggestionRepository.save(suggestion);
    }

    // Overloaded method for backward compatibility
    public void upvoteSuggestion(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        upvoteSuggestion(id, authentication);
    }

    public void deleteSuggestion(Long id, Authentication authentication) {
        User currentUser = userService.getCurrentUser(authentication);
        if (currentUser == null) {
            throw new SecurityException("Authentication required to delete suggestions");
        }

        Suggestion suggestion = suggestionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found with id: " + id));

        suggestionRepository.delete(suggestion);
    }

    // Overloaded method for backward compatibility
    public void deleteSuggestion(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        deleteSuggestion(id, authentication);
    }

    private SuggestionDTO convertToDTO(Suggestion suggestion) {
        SuggestionDTO dto = new SuggestionDTO();
        dto.setId(suggestion.getId());
        dto.setTitle(suggestion.getTitle());
        dto.setDescription(suggestion.getDescription());
        dto.setCategory(suggestion.getCategory());
        dto.setStatus(suggestion.getStatus());
        dto.setUserEmail(suggestion.getUser().getEmail());
        dto.setUserName(suggestion.getUser().getFirstName() + " " + suggestion.getUser().getLastName());
        dto.setCreatedAt(suggestion.getCreatedAt().format(formatter));
        dto.setUpvotes(suggestion.getUpvotes());
        dto.setAdminFeedback(suggestion.getAdminFeedback());
        return dto;
    }
}