package org.ilgcc.app.data.importer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceOrganizationImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH.mm.ss");

    private static final String SQL_BEGIN = "BEGIN;\n";

    private static final String SQL_TRUNCATE = "\n\tTRUNCATE TABLE resource_organizations CASCADE;\n";

    private static final String SQL_INSERT = "\tINSERT INTO resource_organizations (resource_org_id, name, street_address, city, state, zip_code, caseload_code, phone, sda) VALUES\n";

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

            Map<BigInteger, BigInteger> resourceOrgToSDAMapping = doResourceOrgSDAMapping();
            if (resourceOrgToSDAMapping.isEmpty()) {
                System.out.println("No resource organization to SDA mappings were found. Unable to generate SQL.");
                return;
            } else {
                System.out.println("Adding resource organization to SDA mappings to SQL script.");
            }

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
                    String resourceOrgId = values[0];
                    resourceOrgIdsAdded.add(resourceOrgId);
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
                            BigInteger orgId = new BigInteger(resourceOrgId);
                            if (resourceOrgToSDAMapping.containsKey(orgId)) {
                                sb.append(",'").append(resourceOrgToSDAMapping.get(orgId)).append("'");
                            } else {
                                sb.append(",'").append("NULL").append("'");
                            }
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

    private static Map<BigInteger, BigInteger> doResourceOrgSDAMapping() throws IOException {
        Path directoryPath = ImporterUtils.createDataImportDirectory();
        Path resourceOrgSDAData = directoryPath.resolve("resource-org-sda-data.csv");
        Map<BigInteger, BigInteger> resourceOrgToSDAMapping = new HashMap<>();

        if (Files.exists(resourceOrgSDAData)) {
            System.out.println("data-import/resource-org-sda-data.csv exists.");

            List<String> resourceOrgSDADataLines = Files.readAllLines(resourceOrgSDAData);
            System.out.println("\n\nThere are " + resourceOrgSDADataLines.size() + " rows in " + resourceOrgSDAData);

            int duplicates = 0;
            for (int j = 1; j < resourceOrgSDADataLines.size(); j++) {
                String[] values = resourceOrgSDADataLines.get(j).split(",");
                try {
                    BigInteger resourceOrgId = new BigInteger(values[0]);
                    BigInteger sda = new BigInteger(values[1]);

                    if (resourceOrgToSDAMapping.containsKey(resourceOrgId)) {
                        if (!resourceOrgToSDAMapping.get(resourceOrgId).equals(sda)) {
                            System.out.println("Duplicate Resource Org ID " + resourceOrgId + ". Already has " + resourceOrgToSDAMapping.get(resourceOrgId) + ". Trying to store " + sda);
                        } else {
                            System.out.println(resourceOrgId + " " + sda);
                            duplicates++;
                        }
                        continue;
                    }
                    resourceOrgToSDAMapping.put(resourceOrgId, sda);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            System.out.println("There are " + resourceOrgToSDAMapping.size() + " Ids mapped from " + resourceOrgSDAData);
            System.out.println("There were " + duplicates + " duplicates.");
        } else {
            System.out.println("data-import/resource-org-sda-data.csv does not exist!");
        }
        return resourceOrgToSDAMapping;
    }
}
