package com.example.Annuaire.Exceptions;

public class InvalidCSVFormatException extends CSVProcessingException {
    public InvalidCSVFormatException(String message) {
        super(message, null);
    }
}