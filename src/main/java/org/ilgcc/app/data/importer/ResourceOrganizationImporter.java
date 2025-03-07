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

public class ResourceOrganizationImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH.mm.ss");

    private static final String SQL_BEGIN = "BEGIN;\n";

    private static final String SQL_TRUNCATE = "\n\tTRUNCATE TABLE resource_organizations CASCADE;\n";

    private static final String SQL_INSERT = "\tINSERT INTO resource_organizations (resource_org_id, name, street_address, city, state, zip_code, caseload_code, phone) VALUES\n";

    private static final String SQL_COMMIT = "COMMIT;\n\n";

    private static final List<String> COLUMN_HEADERS = List.of("RSRCE_ID", "RSRCE_LONG_NAME", "ADR", "CITY", "ST", "ZIP",
            "CSLD_CD", "TELE_NUM");

    private static final List<String> INTEGER_HEADERS = List.of("RSRCE_ID");

    public static void main(String[] args) {
        String fileNameAndPath = args[0];
        if (fileNameAndPath == null || fileNameAndPath.isEmpty()) {
            System.out.println("Please provide a file name for the new data to import.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileNameAndPath));
            System.out.println("\n\nThere are " + lines.size() + " rows in " + fileNameAndPath);

            if (!COLUMN_HEADERS.equals(Arrays.asList(lines.getFirst().split(",")))) {
                System.out.println("Column headers have changed. Unable to generate SQL because the data format has changed.");
                return;
            }

            Set<Integer> integerIndices = new HashSet<>();
            for (String columnHeader : INTEGER_HEADERS) {
                int index = COLUMN_HEADERS.indexOf(columnHeader);
                if (index != -1) {
                    integerIndices.add(index);
                }
            }

            System.out.println("\nEverything looks ok in the CSV format! Preparing to generate SQL.");

            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(formatter);
            String sqlFileName = "resource-organization-data-" + timestamp + ".sql";
            System.out.println("SQL will be written to " + sqlFileName);

            Path directoryPath = ImporterUtils.createDataImportDirectory();
            Path path = directoryPath.resolve(sqlFileName);

            // Create the file and write content to it
            Files.write(path, SQL_BEGIN.getBytes(), StandardOpenOption.CREATE);
            Files.write(path, SQL_TRUNCATE.getBytes(), StandardOpenOption.APPEND);
            Files.write(path, SQL_INSERT.getBytes(), StandardOpenOption.APPEND);

            Set<String> resourceOrgIdsAdded = new HashSet<>();
            for (int j = 1; j < lines.size(); j++) {
                String[] values = ImporterUtils.getValuesFromCSVRow(lines.get(j));

                StringBuilder sb = new StringBuilder("\t(");

                if (resourceOrgIdsAdded.contains(values[0])) {
                    System.out.println("Skipping resource organization because it is duplicated: " + values[0]);
                } else {
                    resourceOrgIdsAdded.add(values[0]);
                    for (int i = 0; i < values.length; i++) {
                        StringBuilder valueToInsert = new StringBuilder();
                        if (values[i] == null || values[i].isBlank()) {
                            valueToInsert.append("NULL");
                        } else if (integerIndices.contains(i)) {
                            valueToInsert.append(values[i].trim());
                        } else {
                            String cleanedValue = ImporterUtils.getCleanedValue(values[i]);
                            valueToInsert.append("'").append(cleanedValue).append("'");
                        }
                        sb.append(valueToInsert);
                        if (i < values.length - 1) {
                            sb.append(", ");
                        } else {
                            String endOfLineCharacter = j % (lines.size() - 1) == 0 ? ";" : ",";
                            sb.append(")").append(endOfLineCharacter).append("\n");
                        }
                    }

                    Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
                }

            }
            Files.write(path, SQL_COMMIT.getBytes(), StandardOpenOption.APPEND);

            System.out.println(sqlFileName + " created and SQL written successfully.");

        } catch (IOException e) {
            System.out.println("Error while generating SQL: \n\n" + e.getMessage());
        }
    }
}
