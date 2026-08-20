package com.example.Annuaire.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fraud_alerts")
public class FraudAlert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "alert_type", nullable = false)
    private String alertType;
    
    @Column(nullable = false, length = 500)
    private String description;
    
    @Column(name = "reimbursement_id")
    private Long reimbursementId;
    
    @Column(name = "detected_at", nullable = false)
    private LocalDateTime detectedAt;
    
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertStatus status;
    
    @Column(name = "resolved_by")
    private String resolvedBy;
    
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;
    
    @Column(name = "resolution_notes", length = 1000)
    private String resolutionNotes;
    
    public enum AlertStatus {
        OPEN, INVESTIGATING, CONFIRMED_FRAUD, FALSE_POSITIVE, RESOLVED
    }
    public FraudAlert() {
        this.detectedAt = LocalDateTime.now();
        this.status = AlertStatus.OPEN;
    }
    
    public FraudAlert(String alertType, String description, Long reimbursementId) {
        this();
        this.alertType = alertType;
        this.description = description;
        this.reimbursementId = reimbursementId;
    }}