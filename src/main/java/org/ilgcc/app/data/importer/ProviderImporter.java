package org.ilgcc.app.data.importer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProviderImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH.mm.ss");
    private static final String SQL_INSERT = "\tINSERT INTO providers (provider_id, type, name, dba_name, street_address, city, state, zip_code, status) VALUES\n";
    private static final String SQL_BEGIN = "BEGIN;\n";
    private static final String SQL_COMMIT = "COMMIT;\n\n";

    private static final String SQL_TRUNCATE =  SQL_BEGIN + "\tTRUNCATE TABLE providers;\n" + SQL_COMMIT;

    private static final List<String> COLUMN_HEADERS = List.of("RSRCE_ID", "Provider Type", "RSRCE_NAME", "DO_BUSN_AS_NAME",
            "STR_ADR", "CITY", "ST", "ZIP", "Date of Last Approval", "Maintaining R&R", "Provider Status");

    private static final List<String> EXCLUDED_COLUMN_HEADERS = List.of("Date of Last Approval", "Maintaining R&R");

    public static void main(String[] args) {
        String fileNameAndPath = args[0];
        if (fileNameAndPath == null || fileNameAndPath.isEmpty()) {
            System.out.println("Please provide a file name.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileNameAndPath));
            System.out.println("\n\nThere are " + lines.size() + " lines in " + fileNameAndPath);

            if (!COLUMN_HEADERS.equals(Arrays.asList(lines.get(0).split(",")))) {
                System.out.println("Column headers have changed. Unable to generate SQL because the data format has changed.");
                return;
            }

            Set<Integer> excludedColumnsIndices = new HashSet<>();
            for (String excludedColumnHeader : EXCLUDED_COLUMN_HEADERS) {
                int index = COLUMN_HEADERS.indexOf(excludedColumnHeader);
                if (index != -1) { // Check if the item exists in foo
                    excludedColumnsIndices.add(index);
                }
            }

            System.out.println("\nPreparing to generate SQL.");

            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(formatter);
            String sqlFileName = "providers-data-" + timestamp + ".sql";
            System.out.println("SQL will be written to " + sqlFileName);

            Path path = Paths.get(sqlFileName);

            // Create the file and write content to it
            Files.write(path, SQL_TRUNCATE.getBytes(), StandardOpenOption.CREATE);
            Files.write(path, SQL_BEGIN.getBytes(), StandardOpenOption.APPEND);
            Files.write(path, SQL_INSERT.getBytes(), StandardOpenOption.APPEND);
            for (int j = 1; j < lines.size(); j++) {
                // If there's a line with an element wrapped in double quotes that also has a comma, remove that comma
                // For example: ACME Daycare, "Smith, Thomas", 1 Main St --> ACME Daycare, "Smith Thomas", 1 Main St
                String line = lines.get(j).replaceAll("\"([^\"]*?),\\s*([^\"]*)\"", "\"$1 $2\"");
                String[] values = line.split(","); // Split by comma

                StringBuilder sb = new StringBuilder("\t(");
                for (int i = 0; i < values.length; i++) {
                    if (excludedColumnsIndices.contains(i)) {
                        continue;
                    }

                    StringBuilder valueToInsert = new StringBuilder();
                    if (values[i] == null || values[i].isBlank()) {
                        valueToInsert.append("NULL");
                    } else {
                        // escape ' to '', remove ", remove whitespace
                        String cleanedValue = values[i].replaceAll("(?<!')'|'(?!')", "''").replaceAll("\"","").trim();
                        if (i == 0) {
                            valueToInsert.append(cleanedValue);
                        } else {
                            valueToInsert.append("'").append(cleanedValue).append("'");
                        }
                    }
                    sb.append(valueToInsert);
                    if (i < values.length - 1) {
                        sb.append(", ");
                    } else {
                        if (j % 50 == 0) {
                            sb.append(");\n");
                            sb.append(SQL_COMMIT);
                            sb.append(SQL_BEGIN);
                            sb.append(SQL_INSERT);
                        } else {
                            String endOfLineCharacter = j % (lines.size() - 1) == 0 ? ";" : ",";
                            sb.append(")").append(endOfLineCharacter).append("\n");
                        }
                    }
                }
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
            }
            Files.write(path, SQL_COMMIT.getBytes(), StandardOpenOption.APPEND);

            System.out.println(sqlFileName + " created and SQL written successfully.");

        } catch (IOException e) {
            System.out.println("Error while generating SQL: \n\n" + e.getMessage());
        }
    }
}
