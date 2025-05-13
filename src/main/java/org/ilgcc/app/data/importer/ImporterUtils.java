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

    public static String[] getValuesFromCSVRow(String row) {
        // If there's a line with an element wrapped in double quotes that also has a comma, remove that comma
        // For example: ACME Daycare, "Smith, Thomas", 1 Main St --> ACME Daycare, "Smith Thomas", 1 Main St
        String line = row.replaceAll("\"([^\"]*?),\\s*([^\"]*)\"", "\"$1 $2\"");

        // The limit of -1 as the second param means that if there's a trailing blank value in the comma separated list, it'll
        // be returned. For example, if the String to split is: "Marc,Testing,123,,,"
        // Without the -1, the split would return ["Marc", "Testing","123"]
        // With the -1, the split would return ["Marc", "Testing","123","","",""]
        // Since the Providers data can end with empty values for SSN and FEIN per row, we want to/need to return the empty string
        // values in the String[]
        return line.split(",", -1);
    }

    public static String getCleanedValue(String value) {
        // escape ' to ''
        // remove "
        // remove whitespace
        return value.replaceAll("(?<!')'|'(?!')", "''").replaceAll("\"", "").trim();
    }
}
