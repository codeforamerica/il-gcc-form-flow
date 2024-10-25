package org.ilgcc.app.data.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ProviderImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH.mm.ss");
    private static final String SQL_INSERT = "INSERT INTO providers (provider_id, type, name, dba_name, street_address, city, state, zip_code, status) VALUES\n";

    public static void main(String[] args) {
        String fileNameAndPath = args[0];
        if (fileNameAndPath == null || fileNameAndPath.isEmpty()) {
            System.out.println("Please provide a file name.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileNameAndPath));
            System.out.println("\n\nThere are " + lines.size() + " lines in " + fileNameAndPath);
            System.out.println("\nPreparing to generate SQL.");

            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(formatter);
            String sqlFileName = "providers-data-" + timestamp + ".sql";
            System.out.println("SQL will be written to " + sqlFileName);

            Path path = Paths.get(sqlFileName);

            // Create the file and write content to it
            Files.write(path, "BEGIN;\n\n".getBytes(), StandardOpenOption.CREATE);

            Files.write(path, SQL_INSERT.getBytes(), StandardOpenOption.APPEND);
            for (String line : lines) {
                String[] values = line.split(","); // Split by comma

                StringBuilder sb = new StringBuilder("(");
                for (int i = 0; i < values.length; i++) {
                    StringBuilder valueToInsert = new StringBuilder();
                    if (values[i] == null || values[i].isBlank()) {
                        valueToInsert.append("NULL");
                    } else {
                        if (i == 0) {
                            valueToInsert.append(values[i].trim());
                        } else {
                            valueToInsert.append("'").append(values[i].trim()).append("'");
                        }
                    }
                    sb.append(valueToInsert);
                    if (i < values.length - 1) {
                        sb.append(", ");
                    } else {
                        sb.append(")\n");
                    }
                }
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
//                System.out.println(); // New line after each row
            }
            Files.write(path, "\n\nCOMMIT;".getBytes(), StandardOpenOption.APPEND);

            System.out.println(sqlFileName + " created and SQL written successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
