package com.example.Annuaire.Models;

import com.example.Annuaire.Models.Suggestion.SuggestionStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SuggestionDTO {

    private Long id;

    private String title;

    private String description;

    private String category;

    private SuggestionStatus status;

    private String userEmail;

    private String userName;

    private String createdAt;

    private Integer upvotes;

    private String adminFeedback;

}
