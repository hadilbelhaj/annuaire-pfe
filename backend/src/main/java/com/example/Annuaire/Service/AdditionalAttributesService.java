package com.example.Annuaire.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.Annuaire.Repository.AdditionalAttributesPsRepository;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service

public class AdditionalAttributesService {

    private final AdditionalAttributesPsRepository additionalAttributesPsRepository;

    @Autowired
    public AdditionalAttributesService(AdditionalAttributesPsRepository additionalAttributesPsRepository) {
        this.additionalAttributesPsRepository = additionalAttributesPsRepository;
    }

}
