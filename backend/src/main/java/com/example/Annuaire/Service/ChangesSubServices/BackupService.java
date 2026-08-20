package com.example.Annuaire.Service.ChangesSubServices;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.Annuaire.Models.BackupHistory;
import com.example.Annuaire.Repository.BackupHistoryRepository;

@Service
public class BackupService {
    @Autowired
    private final BackupHistoryRepository historyRepository;
    private static final Logger LOGGER = LoggerFactory.getLogger(BackupService.class);

    public BackupService(BackupHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Value("${node.backup.folder}")
    private String backupFolder;

    @Value("${backup.tables}")
    private String[] tables;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    @Value("${database.name}")
    private String dbName;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${mysql.dump.path}")
    private String mysqldumpPath;

    private static final int MAX_RETRIES = 3;

    public void createBackup() throws IOException {
        validateDatabaseConnection(); // Ensure DB is reachable

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String backupPath = Paths.get(backupFolder, currentDate + ".sql").toString();

        ensureBackupFolderExists();

        String tableList = Arrays.stream(tables).collect(Collectors.joining(" "));
        String dumpCommand = String.format("\"%s\" -u%s -p%s %s %s", mysqldumpPath, dbUser, dbPass, dbName, tableList);

        String fullCommand = String.format("cmd.exe /c \"%s > %s\"", dumpCommand, backupPath);
        executeCommandWithRetries(fullCommand, "Backup", backupPath);

    }

    public void restoreBackup(String date) throws IOException {
        String backupPathFile = Paths.get(backupFolder, date + ".sql").toString();

        if (!Files.exists(Paths.get(backupPathFile))) {
            throw new IOException("Backup file not found: " + backupPathFile);
        }

        String command = String.format("mysql -u%s -p%s %s < %s", dbUser, dbPass, dbName, backupPathFile);
        String fullCommand = String.format("cmd.exe /c \"%s\"", command);

        executeCommandWithRetries(fullCommand, "Restore", backupPathFile);
        historyRepository.save(new BackupHistory(
                "Backup restored from " + date + ".sql",
                LocalDate.now().atStartOfDay(),
                "SUCCESS"));
    }

    private void ensureBackupFolderExists() throws IOException {
        Path backupDir = Paths.get(backupFolder);
        if (Files.notExists(backupDir)) {
            Files.createDirectories(backupDir);
            LOGGER.info("Created backup directory: {}", backupFolder);
        } else if (!Files.isDirectory(backupDir)) {
            throw new IOException("Backup path exists but is not a directory: " + backupFolder);
        }
    }

    private void executeCommandWithRetries(String command, String operation, String filePath) throws IOException {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                Process process = Runtime.getRuntime().exec(command);
                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    LOGGER.info("{} successful: {}", operation, filePath);
                    return;
                }

                String errorOutput = readProcessErrorStream(process);
                LOGGER.error("{} failed (attempt {}): {}", operation, attempt + 1, errorOutput);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException(operation + " process interrupted: " + e.getMessage());
            }

            attempt++;
            try {
                Thread.sleep(3000); // Wait before retrying
            } catch (InterruptedException ignored) {
            }
        }
        throw new IOException(operation + " failed after " + MAX_RETRIES + " attempts.");
    }

    private String readProcessErrorStream(Process process) throws IOException {
        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        String line;
        while ((line = stdError.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }
        return errorOutput.toString();
    }

    private void validateDatabaseConnection() throws IOException {
        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            if (!connection.isValid(2)) {
                throw new IOException("Database connection is not valid.");
            }
            LOGGER.info("Database connection verified.");
        } catch (SQLException e) {
            throw new IOException("Database connection failed: " + e.getMessage(), e);
        }
    }
}
