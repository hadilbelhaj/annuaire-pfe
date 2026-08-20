package com.example.Annuaire.Utils;

import java.util.Random;

public class TaxNumberGenerator {

    private static final Random random = new Random();

    public static String generateValidTaxNumber() {
        int mainNumber = 1000000 + random.nextInt(9000000); // Ensure 7 digits
        int controlDigit = computeCheckDigit(String.valueOf(mainNumber));
        return mainNumber + "-" + controlDigit;
    }

    private static int computeCheckDigit(String mainNumber) {
        int sum = 0;
        for (char c : mainNumber.toCharArray()) {
            sum += Character.getNumericValue(c);
        }
        return sum % 9; // Modulo 9 checksum
    }
}
