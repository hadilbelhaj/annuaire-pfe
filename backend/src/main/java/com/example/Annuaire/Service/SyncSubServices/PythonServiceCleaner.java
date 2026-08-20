package com.example.Annuaire.Service.SyncSubServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.*;

@Service
public class PythonServiceCleaner {

    @Value("${python.clean.folder}")
    private String cleanFolder;

    public PythonServiceCleaner() {
    }

    public String runPythonCleaner(String inputFile) throws IOException, InterruptedException {
        ClassPathResource resource = new ClassPathResource("scripts/clean.py");
        File scriptFile = resource.getFile();

        ProcessBuilder pb = new ProcessBuilder(
                "python3",
                scriptFile.getAbsolutePath(),
                inputFile,
                cleanFolder);

        pb.redirectErrorStream(true);

        Process process = pb.start();

        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python script exited with error code: " + exitCode + "\n" + output);
        }
        return output.toString();
    }
}
