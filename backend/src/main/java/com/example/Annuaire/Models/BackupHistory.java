package com.example.Annuaire.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "backup_history")
public class BackupHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String backupName;

    @Column(nullable = false)
    private LocalDateTime backupDate;

    @Column(nullable = false)
    private String status;

    // Constructors
    public BackupHistory() {
    }

    public BackupHistory(String backupName, LocalDateTime backupDate, String status) {
        this.backupName = backupName;
        this.backupDate = backupDate;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBackupName() {
        return backupName;
    }

    public void setBackupName(String backupName) {
        this.backupName = backupName;
    }

    public LocalDateTime getBackupDate() {
        return backupDate;
    }

    public void setBackupDate(LocalDateTime backupDate) {
        this.backupDate = backupDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
