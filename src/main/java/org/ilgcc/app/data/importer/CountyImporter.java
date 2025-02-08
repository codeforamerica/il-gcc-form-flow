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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CountyImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH.mm.ss");

    private static final String SQL_INSERT = "\tINSERT INTO zip_codes (zip_code, city, county, fips_county_code, dpa_county_code, caseload_code) VALUES\n";
    private static final String SQL_BEGIN = "BEGIN;\n";
    private static final String SQL_COMMIT = "COMMIT;\n\n";

    private static final String SQL_CONFLICT =
            "\nON CONFLICT (zip_code)\n"
            + "DO UPDATE SET\n"
            + "\tzip_code = excluded.zip_code,\n"
            + "\tcity = excluded.city,\n"
            + "\tcounty = excluded.county,\n"
            + "\tfips_county_code = excluded.fips_county_code,\n"
            + "\tdpa_county_code = excluded.dpa_county_code,\n"
            + "\tcaseload_code= excluded.caseload_code;\n";

    private static final List<String> COLUMN_HEADERS = List.of("ZIP", "CITY_NAME", "CNTY_NAME", "FIPS_CNTY_CD", "DPA_CNTY_CD",
            "CSLD_CD");

    private static final List<String> INTEGER_HEADERS = List.of("FIPS_CNTY_CD", "DPA_CNTY_CD");


    public static void main(String[] args) {
        String fileNameAndPath = args[0];
        if (fileNameAndPath == null || fileNameAndPath.isEmpty()) {
            System.out.println("Please provide a file name for the new data to import.");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileNameAndPath));
            System.out.println("\n\nThere are " + lines.size() + " rows in " + fileNameAndPath);

            if (!COLUMN_HEADERS.equals(Arrays.asList(lines.get(0).split(",")))) {
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
            String sqlFileName = "county-data-" + timestamp + ".sql";
            System.out.println("SQL will be written to " + sqlFileName);

            Path path = Paths.get(sqlFileName);

            // Create the file and write content to it
            Files.write(path, SQL_BEGIN.getBytes(), StandardOpenOption.CREATE);
            Files.write(path, SQL_INSERT.getBytes(), StandardOpenOption.APPEND);

            Set<BigInteger> idsInBatch = new HashSet<>();
            for (int j = 1; j < lines.size(); j++) {
                String line = lines.get(j);
                String[] values = line.split(","); // Split by comma

                StringBuilder sb = new StringBuilder("\t(");
                for (int i = 0; i < values.length; i++) {
                    StringBuilder valueToInsert = new StringBuilder();
                    if (values[i] == null || values[i].isBlank()) {
                        valueToInsert.append("NULL");
                    } else if (integerIndices.contains(i)) {
                        valueToInsert.append(values[i].trim());
                    } else {
                        valueToInsert.append("'").append(values[i].trim()).append("'");
                    }
                    sb.append(valueToInsert);
                    if (i < values.length - 1) {
                        sb.append(", ");
                    } else {
                        if (j % 20000 == 0) {
                            // Batches of 20000
                            // If there's a duplicated row, just skip over it so the batch does not get rolled back
                            sb.append(")\n");
                            sb.append(SQL_CONFLICT);
                            sb.append(SQL_COMMIT);
                            sb.append(SQL_BEGIN);
                            sb.append(SQL_INSERT);

                            idsInBatch.clear();
                        } else {
                            String endOfLineCharacter = j % (lines.size() - 1) == 0 ? SQL_CONFLICT : ",";
                            sb.append(")").append(endOfLineCharacter).append("\n");
                        }
                    }
                }
                Files.write(path, sb.toString().getBytes(), StandardOpenOption.APPEND);
            }
            Files.write(path, SQL_COMMIT.getBytes(), StandardOpenOption.APPEND);

            Files.write(path, SQL_BEGIN.getBytes(), StandardOpenOption.APPEND);
            Files.write(path, SQL_COMMIT.getBytes(), StandardOpenOption.APPEND);

            System.out.println(sqlFileName + " created and SQL written successfully.");

        } catch (IOException e) {
            System.out.println("Error while generating SQL: \n\n" + e.getMessage());
        }
    }
}
