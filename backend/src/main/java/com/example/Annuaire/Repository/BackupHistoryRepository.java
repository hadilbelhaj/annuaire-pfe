package com.example.Annuaire.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.Annuaire.Models.BackupHistory;

@Repository
public interface BackupHistoryRepository extends JpaRepository<BackupHistory, Long> {

}
