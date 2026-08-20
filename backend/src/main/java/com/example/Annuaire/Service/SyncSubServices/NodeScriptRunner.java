package com.example.Annuaire.Service.SyncSubServices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NodeScriptRunner {

    @Value("${node.script.path}")
    private String nodeScriptPath;

    @Value("${node.api.url}")
    private String nodeApiUrl;

    @Value("${node.data.folder}")
    private String nodeDataFolder;

    @Value("${node.changes.folder}")
    private String nodeChangesFolder;

    public String runNodeScript() throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(
                "node",
                nodeScriptPath,
                nodeApiUrl,
                nodeDataFolder,
                nodeChangesFolder);
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
            throw new RuntimeException("Node script exited with error code: " + exitCode + output.toString());
        }
        return output.toString();
    }
}
