package com.example.Annuaire.Service.SyncSubServices;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class DataFileService {

    @Value("${node.data.folder}")
    private String dataFolder;

    public File getTodayJsonFile() {
        String todayDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String filePath = Paths.get(dataFolder, todayDate + ".json").toString();
        File jsonFile = new File(filePath);

        if (jsonFile.exists()) {
            return jsonFile;
        } else {
            throw new RuntimeException("Today's JSON file not found: " + filePath);
        }
    }
}
