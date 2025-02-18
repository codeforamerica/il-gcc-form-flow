package org.ilgcc.app.data.importer;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ProviderImporter {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH.mm.ss");
    private static final DateTimeFormatter dateColumnFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");

    private static final String SQL_INSERT = "\tINSERT INTO providers (provider_id, type, name, dba_name, street_address, city, state, zip_code, date_of_last_approval, resource_org_id, status) VALUES\n";
    private static final String SQL_BEGIN = "BEGIN;\n";
    private static final String SQL_COMMIT = "COMMIT;\n\n";

    private static final String SQL_DELETE = "\tDELETE FROM providers WHERE provider_id IN ";
    private static final String SQL_CONFLICT = "\nON CONFLICT (provider_id)\n" +
            "DO UPDATE SET\n" +
            "\ttype = excluded.type,\n" +
            "\tname = excluded.name,\n" +
            "\tdba_name = excluded.dba_name,\n" +
            "\tstreet_address = excluded.street_address,\n" +
            "\tcity = excluded.city,\n" +
            "\tstate = excluded.state,\n" +
            "\tzip_code = excluded.zip_code,\n" +
            "\tdate_of_last_approval = excluded.date_of_last_approval,\n" +
            "\tresource_org_id = excluded.resource_org_id,\n" +
            "\tstatus = excluded.status;\n";

    private static final List<String> COLUMN_HEADERS = List.of("RSRCE_ID", "Provider Type", "RSRCE_NAME", "DO_BUSN_AS_NAME",
            "STR_ADR", "CITY", "ST", "ZIP", "Date of Last Approval", "Maintaining R&R", "Provider Status");

    private static final List<String> EXCLUDED_COLUMN_HEADERS = List.of("460328258720008");
    private static final List<String> DATE_COLUMN_HEADERS = List.of("Date of Last Approval");

    private static final List<String> EXCLUDED_IDS = List.of();

    public static void main(String[] args) {
        String fileNameAndPath = args[0];
        if (fileNameAndPath == null || fileNameAndPath.isEmpty()) {
            System.out.println("Please provide a file name for the new data to import.");
            return;
        }

        Set<BigInteger> priorIds = new HashSet<>();

        if (args.length > 1) {
            // If there's more than 1 command line argument, get the 2nd argument and use it
            // as the path and filename for the prior data set
            String priorFileNameAndPath = args[1];
            if (priorFileNameAndPath == null || priorFileNameAndPath.isEmpty()) {
                // If somehow there's a second parameter but it's blank, don't do anything
                System.out.println("No old data supplied.");
            } else {
                System.out.println("Old data supplied, building Id list!");
                try {
                    List<String> lines = Files.readAllLines(Paths.get(priorFileNameAndPath));
                    for (int i = 1; i < lines.size(); i++) {
                        // Skip the header row in the csv, and then just get everything in the first column
                        // and dump all those Ids into a Set
                        String[] columns = lines.get(i).split(",");
                        priorIds.add(new BigInteger(columns[0]));
                    }
                    System.out.println("Found " + priorIds.size() + " Ids in old data file.");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        try {
            List<String> lines = Files.readAllLines(Paths.get(fileNameAndPath));
            System.out.println("\n\nThere are " + lines.size() + " rows in " + fileNameAndPath);

            if (!COLUMN_HEADERS.equals(Arrays.asList(lines.get(0).split(",")))) {
                System.out.println("Column headers have changed. Unable to generate SQL because the data format has changed.");
                return;
            }

            Set<Integer> excludedColumnsIndices = new HashSet<>();
            for (String excludedColumnHeader : EXCLUDED_COLUMN_HEADERS) {
                int index = COLUMN_HEADERS.indexOf(excludedColumnHeader);
                if (index != -1) {
                    excludedColumnsIndices.add(index);
                }
            }

            Set<Integer> dateColumnsIndices = new HashSet<>();
            for (String dateColumnHeader : DATE_COLUMN_HEADERS) {
                int index = COLUMN_HEADERS.indexOf(dateColumnHeader);
                if (index != -1) {
                    dateColumnsIndices.add(index);
                }
            }

            System.out.println("\nEverything looks ok in the CSV format! Preparing to generate SQL.");

            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(formatter);
            String sqlFileName = "providers-data-" + timestamp + ".sql";
            System.out.println("SQL will be written to " + sqlFileName);

            Path path = Paths.get(sqlFileName);

            // Create the file and write content to it
            Files.write(path, SQL_BEGIN.getBytes(), StandardOpenOption.CREATE);
            Files.write(path, SQL_INSERT.getBytes(), StandardOpenOption.APPEND);

            Set<BigInteger> idsInBatch = new HashSet<>();
            for (int j = 1; j < lines.size(); j++) {
                // If there's a line with an element wrapped in double quotes that also has a comma, remove that comma
                // For example: ACME Daycare, "Smith, Thomas", 1 Main St --> ACME Daycare, "Smith Thomas", 1 Main St
                String line = lines.get(j).replaceAll("\"([^\"]*?),\\s*([^\"]*)\"", "\"$1 $2\"");
                String[] values = line.split(","); // Split by comma

                if (EXCLUDED_IDS.contains(values[0])) {

                    // These Ids are excluded and should not ever be written to our DB, most likely
                    // because they are for testing and training purposes only and are not real providers

                    System.out.println("Ignoring ID " + values[0]);
                    continue;
                }

                if (idsInBatch.contains(new BigInteger(values[0]))) {
                    // Skip Ids that we've already added in this batch, because of how an INSERT/UPDATE works within a
                    // batch. Can't update something that is already being inserted in the same batch.
                    System.out.println("Skipping ID " + values[0]);
                    continue;
                } else {
                    idsInBatch.add(new BigInteger(values[0]));
                }

                priorIds.remove(new BigInteger(values[0]));

                StringBuilder sb = new StringBuilder("\t(");
                for (int i = 0; i < values.length; i++) {
                    if (excludedColumnsIndices.contains(i)) {
                        // Skip values in the excluded columns
                        continue;
                    }

                    StringBuilder valueToInsert = new StringBuilder();
                    if (values[i] == null || values[i].isBlank()) {
                        valueToInsert.append("NULL");
                    } else {
                        // escape ' to ''
                        // remove "
                        // remove whitespace
                        String cleanedValue = values[i].replaceAll("(?<!')'|'(?!')", "''").replaceAll("\"", "").trim();
                        if (i == 0) {
                            // No need to wrap the id with single quotes
                            valueToInsert.append(cleanedValue);
                        } else if (dateColumnsIndices.contains(i)) {
                            LocalDate localDate = LocalDate.parse(cleanedValue, dateColumnFormatter);
                            ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.of("America/Chicago"));
                            OffsetDateTime offsetDateTime = zonedDateTime.toOffsetDateTime();

                            valueToInsert.append("'").append(offsetDateTime).append("'");
                        } else {
                            // Every other field, other than the id and dates, is just a VARCHAR so wrap those values with single quotes
                            valueToInsert.append("'").append(cleanedValue).append("'");
                        }
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

            if (!priorIds.isEmpty()) {
                String ids = priorIds.stream().map(BigInteger::toString).collect(Collectors.joining(","));
                Files.write(path, SQL_BEGIN.getBytes(), StandardOpenOption.APPEND);
                Files.write(path, (SQL_DELETE + "(" + ids + ");\n").getBytes(), StandardOpenOption.APPEND);
                Files.write(path, SQL_COMMIT.getBytes(), StandardOpenOption.APPEND);
            }

            System.out.println(sqlFileName + " created and SQL written successfully.");

        } catch (IOException e) {
            System.out.println("Error while generating SQL: \n\n" + e.getMessage());
        }
    }
}