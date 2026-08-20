package com.example.Annuaire.Service.Fraud;

import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CollusionFraudDetectionService {

    private final Driver neo4jDriver;

    @Autowired
    public CollusionFraudDetectionService(Driver neo4jDriver) {
        this.neo4jDriver = neo4jDriver;
    }

    public List detectFrequentVisitsInShortPeriod(int threshold, int maxDays) {
        String query = """
                MATCH (a:Adherent)-[s:SUBMITTED]->(c:Claim)<-[p:PROCESSED]-(d:Doctor)
                WITH a, d, c
                ORDER BY c.date
                WITH a, d, collect(c) as claims,
                    min(c.date) as firstClaimDate,
                    max(c.date) as lastClaimDate,
                    count(c) as totalVisits
                UNWIND range(0, size(claims)-1) as i
                WITH a, d, claims[i] as startClaim, claims, i, firstClaimDate, lastClaimDate, totalVisits
                UNWIND range(i+1, size(claims)-1) as j
                WITH a, d, startClaim, claims[j] as endClaim, firstClaimDate, lastClaimDate, totalVisits
                WITH a, d, startClaim, endClaim,
                    duration.between(datetime(startClaim.date), datetime(endClaim.date)).days as daysBetween,
                    firstClaimDate, lastClaimDate, totalVisits
                WHERE daysBetween <= $maxDays
                WITH a, d, startClaim, collect(endClaim) as recentClaims, firstClaimDate, lastClaimDate, totalVisits
                WITH a, d, startClaim, recentClaims, size(recentClaims) as recentClaimCount, firstClaimDate, lastClaimDate, totalVisits
                WHERE recentClaimCount >=$threshold
                WITH a, d, count(startClaim) as suspiciousPatterns, firstClaimDate, lastClaimDate, totalVisits
                WHERE suspiciousPatterns > 0
                RETURN a.name AS adherent,
                    a.id AS adherentId,
                    d.name AS doctor,
                    d.id AS doctorId,
                    firstClaimDate as firstVisitDate,
                    lastClaimDate as lastVisitDate,
                    totalVisits as numberOfVisits,
                    suspiciousPatterns as fraudScore
                ORDER BY fraudScore DESC
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("threshold", threshold); 
        params.put("maxDays", maxDays);

        return executeQuery(query, params);
    }

    public List<Map<String, Object>> detectHighAmountClaims(double threshold) {
        String query = """
                MATCH (a:Adherent)-[:SUBMITTED]->(c:Claim)<-[:PROCESSED]-(d:Doctor)
                WHERE c.amount > $threshold
                RETURN a.name AS adherent, a.id AS adherentId,
                       d.name AS doctor, d.id AS doctorId,
                       c.id AS claimId, c.amount AS amount, c.date AS date
                ORDER BY c.amount DESC
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("threshold", threshold);

        return executeQuery(query, params);
    }

    public List<Map<String, Object>> detectGeographicAnomalies() {
        String query = """
                MATCH (a:Adherent)-[:LIVES_AT]->(aAddr:Address),
                      (d:Doctor)-[:PRACTICES_AT]->(dAddr:Address),
                      (a)-[:SUBMITTED]->(c:Claim)<-[:PROCESSED]-(d)
                WHERE aAddr.city <> dAddr.city
                RETURN a.name AS adherent, a.id AS adherentId,
                       aAddr.city AS adherentCity,
                       d.name AS doctor, d.id AS doctorId,
                       dAddr.city AS doctorCity,
                       count(c) AS claimCount
                ORDER BY claimCount DESC
                """;

        return executeQuery(query, Map.of());
    }

    public List<Map<String, Object>> calculateRiskScores(double riskThreshold) {
        String query = """
                MATCH (a:Adherent)-[:SUBMITTED]->(c:Claim)<-[:PROCESSED]-(d:Doctor)
                WITH a, d,
                     count(c) AS claimCount,
                     sum(c.amount) AS totalAmount,
                     avg(c.amount) AS avgAmount
                WITH a, d, claimCount, totalAmount, avgAmount,
                     CASE
                       WHEN claimCount > 10 THEN 0.5
                       WHEN claimCount > 5 THEN 0.3
                       ELSE 0.1
                     END AS frequency_risk,
                     CASE
                       WHEN avgAmount > 500 THEN 0.4
                       WHEN avgAmount > 300 THEN 0.2
                       ELSE 0.0
                     END AS amount_risk
                WITH a, d, claimCount, totalAmount, avgAmount,
                     frequency_risk + amount_risk AS collusion_risk_score
                WHERE collusion_risk_score > $riskThreshold
                RETURN a.name AS adherent, a.id AS adherentId,
                       d.name AS doctor, d.id AS doctorId,
                       claimCount, totalAmount, avgAmount,
                       collusion_risk_score
                ORDER BY collusion_risk_score DESC
                """;

        Map<String, Object> params = new HashMap<>();
        params.put("riskThreshold", riskThreshold);

        return executeQuery(query, params);
    }

    private List<Map<String, Object>> executeQuery(String query, Map<String, Object> params) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Session session = neo4jDriver.session()) {
            Result result = session.run(query, params);
            result.list().forEach(record -> {
                Map<String, Object> row = new HashMap<>();
                record.keys().forEach(key -> row.put(key, record.get(key).asObject()));
                results.add(row);
            });
        }

        return results;
    }
}