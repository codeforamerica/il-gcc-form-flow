package org.ilgcc.app.data.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImporterUtils {

    public static Path createDataImportDirectory() {
        Path directoryPath = Paths.get("data-import");
        if (Files.notExists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
                System.out.println("Directory created: " + directoryPath);
            } catch (IOException e) {
                System.err.println("Failed to create directory: " + e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Directory already exists: " + directoryPath);
        }

        return directoryPath;
    }
}
