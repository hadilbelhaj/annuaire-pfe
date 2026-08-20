package com.example.Annuaire.Service.Fraud;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.MapAccessor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SuppressWarnings("deprecation")
public class DeductibleTrackingService {

    private static final Logger logger = LoggerFactory.getLogger(DeductibleTrackingService.class);
    private final Driver neo4jDriver;

    @Autowired
    public DeductibleTrackingService(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public Map<String, Object> checkDeductibleStatus(String adherentName) {
        logger.info(adherentName);

        try (Session session = neo4jDriver.session()) {
            return session.readTransaction(tx -> {
                Map<String, Object> params = new HashMap<>();
                params.put("name", adherentName);

                Result result = tx.run(
                        "MATCH (a:Adherent {name:$name })-[:SUBMITTED]->(c:Claim) " +
                                "WITH a, SUM(c.insuranceAmount) as totalInsurancePaid " +
                                "RETURN a.id as id, a.name as name, a.deductible as deductible, " +
                                "totalInsurancePaid, " +
                                "(totalInsurancePaid > a.deductible) as isExceeded, " +
                                "(a.deductible - totalInsurancePaid) as remainingDeductible",
                        params);

                if (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> response = new HashMap<>();
                    response.put("id", record.get("id").asLong());
                    response.put("name", record.get("name").asString());
                    response.put("deductible", record.get("deductible").asDouble());
                    response.put("totalInsurancePaid", record.get("totalInsurancePaid").asDouble());
                    response.put("isExceeded", record.get("isExceeded").asBoolean());
                    response.put("remainingDeductible", record.get("remainingDeductible").asDouble());
                    return response;
                } else {
                    Map<String, Object> notFound = new HashMap<>();
                    notFound.put("error", "Adherent not found or has no claims");
                    return notFound;
                }
            });
        } catch (Exception e) {
            logger.error("Error checking deductible status", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "An error occurred while checking deductible status: " + e.getMessage());
            return error;
        }
    }

    public List<Map<String, Object>> findAdherentsApproachingDeductible(double thresholdAmount) {
        logger.info("Finding adherents approaching deductible threshold by amount: {}", thresholdAmount);

        try (Session session = neo4jDriver.session()) {
            return session.readTransaction(tx -> {
                Map<String, Object> params = new HashMap<>();
                params.put("threshold", thresholdAmount);

                Result result = tx.run(
                        "MATCH (a:Adherent)-[:SUBMITTED]->(c:Claim) " +
                                "WITH a, SUM(c.insuranceAmount) as totalInsurancePaid " +
                                "WHERE a.deductible - totalInsurancePaid > 0 AND a.deductible - totalInsurancePaid <=  "
                                +
                                "RETURN a.id as id, a.name as name, a.deductible as deductible, " +
                                "totalInsurancePaid, (a.deductible - totalInsurancePaid) as remainingAmount " +
                                "ORDER BY remainingAmount ASC",
                        params);

                List<Map<String, Object>> adherents = new ArrayList<>();
                while (result.hasNext()) {
                    Record record = result.next();
                    Map<String, Object> adherent = new HashMap<>();
                    adherent.put("id", record.get("id").asString());
                    adherent.put("name", record.get("name").asString());
                    adherent.put("deductible", record.get("deductible").asDouble());
                    adherent.put("totalInsurancePaid", record.get("totalInsurancePaid").asDouble());
                    adherent.put("remainingAmount", record.get("remainingAmount").asDouble());
                    adherents.add(adherent);
                }

                return adherents;
            });
        } catch (Exception e) {
            logger.error("Error finding adherents approaching deductible", e);
            throw new RuntimeException("Error finding adherents approaching deductible: " + e.getMessage(), e);
        }
    }

    public List<Map<String, Object>> findAdherentsExceedingDeductibleWithClaimDetails(int year) {
        logger.info(
                "Finding all adherents who have exceeded their deductible in year {} with detailed claim information",
                year);

        try (Session session = neo4jDriver.session()) {
            return session.readTransaction(tx -> {
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("year", String.valueOf(year));
                // Corrected Cypher query using substring to extract year
                Result result = tx.run(
                        "MATCH (a:Adherent)-[:SUBMITTED]->(c:Claim) " +
                                "WHERE substring(c.date, 0, 4) = $year " +
                                "WITH a, " +
                                "     collect(c) as claims, " +
                                "     sum(c.insuranceAmount) as totalInsurancePaid, " +
                                "     a.deductible as deductible " +
                                "WHERE totalInsurancePaid > deductible " +
                                "RETURN a.id as id, " +
                                "       a.name as name, " +
                                "       a.region as region, " +
                                "       deductible, " +
                                "       totalInsurancePaid, " +
                                "       (totalInsurancePaid - deductible) as excessAmount, " +
                                "       [claim in claims | {" +
                                "           claimId: claim.id, " +
                                "           amount: claim.amount, " +
                                "           insuranceAmount: claim.insuranceAmount, " +
                                "           date: claim.date" +
                                "       }] as claimsList",
                        parameters);

                List<Map<String, Object>> adherentsExceedingDeductible = new ArrayList<>();

                while (result.hasNext()) {
                    Record record = result.next();

                    // Prepare the adherent information
                    Map<String, Object> adherent = new HashMap<>();
                    adherent.put("id", record.get("id").asLong());
                    adherent.put("name", record.get("name").asString());
                    adherent.put("region", record.get("region").asString());
                    adherent.put("deductible", record.get("deductible").asDouble());
                    adherent.put("totalInsurancePaid", record.get("totalInsurancePaid").asDouble());
                    adherent.put("excessAmount", record.get("excessAmount").asDouble());

                    // Process claims that contribute to exceeding the deductible
                    List<Map<String, Object>> claimsThatExceed = new ArrayList<>();
                    double runningTotal = 0.0;

                    // Handle claims list
                    List<Map<String, Object>> claimsList = record.get("claimsList")
                            .asList(value -> {
                                if (value instanceof Map) {
                                    return (Map<String, Object>) value;
                                } else if (value instanceof org.neo4j.driver.internal.value.MapValue) {
                                    org.neo4j.driver.internal.value.MapValue mapValue = (org.neo4j.driver.internal.value.MapValue) value;
                                    return mapValue.asMap();
                                }
                                throw new IllegalArgumentException("Unexpected value type: " + value.getClass());
                            });
                    for (Map<String, Object> claim : claimsList) {
                        // Extract claim details with null checks
                        Long claimId = claim.get("claimId") != null
                                ? ((Number) claim.get("claimId")).longValue()
                                : null;
                        Double amount = claim.get("amount") != null
                                ? ((Number) claim.get("amount")).doubleValue()
                                : 0.0;
                        Double insuranceAmount = claim.get("insuranceAmount") != null
                                ? ((Number) claim.get("insuranceAmount")).doubleValue()
                                : 0.0;
                        String date = claim.get("date") != null
                                ? claim.get("date").toString()
                                : null;

                        // Calculate running total to identify claims that push over the deductible
                        runningTotal += insuranceAmount;

                        if (runningTotal > record.get("deductible").asDouble()) {
                            // Prepare claim details for output
                            Map<String, Object> claimOutput = new HashMap<>();
                            claimOutput.put("claimId", claimId);
                            claimOutput.put("amount", amount);
                            claimOutput.put("insuranceAmount", insuranceAmount);

                            // Parse and format the date for readability
                            if (date != null) {
                                try {
                                    LocalDateTime dateTime = LocalDateTime.parse(date);
                                    claimOutput.put("date", date);
                                    claimOutput.put("formattedDate", dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
                                } catch (Exception e) {
                                    claimOutput.put("date", date);
                                    claimOutput.put("formattedDate", date);
                                }
                            }

                            claimsThatExceed.add(claimOutput);
                        }
                    }

                    // Add the claims that cause exceeding to the adherent information
                    adherent.put("claimsCausingExcess", claimsThatExceed);

                    adherentsExceedingDeductible.add(adherent);
                }

                return adherentsExceedingDeductible;
            });
        } catch (Exception e) {
            logger.error("Error finding adherents exceeding deductible", e);
            throw new RuntimeException("Error finding adherents exceeding deductible: " + e.getMessage(), e);
        }
    }
}