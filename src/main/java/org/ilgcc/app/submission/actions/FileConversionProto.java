package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import java.io.File;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FileConversionProto implements Action {

    public FileConversionProto() {
    }

    @Override
    public void run(Submission submission) {
        String docFilePath = "/Users/mperlman@codeforamerica.org/Downloads/sample.doc"; // Path to the .doc file
        String outputDir = "/Users/mperlman@codeforamerica.org/Downloads/sampleoutput/";   // Output directory for the PDF

        // Ensure the output directory exists
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        // Build the LibreOffice command
        String[] command = {"/opt/homebrew/bin/soffice", // LibreOffice executable
                "--headless", // Run in headless mode
                "--convert-to", "pdf", // Conversion format
                docFilePath // Input file
        };

        try {
            // Use ProcessBuilder to execute the command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().putAll(System.getenv()); // Inherit parent environment
            processBuilder.environment().put("HOME", System.getProperty("user.home"));
            processBuilder.redirectErrorStream(true); // Merge stderr with stdout

            System.out.println(String.join(" ", command));

            System.out.println("Starting process...");
            // Start the process
            long start = new Date().getTime();

            Process process = processBuilder.start();


            // Wait for the process to complete
            System.out.println("Waiting...");
            int exitCode = process.waitFor();
            long end = new Date().getTime();

            long seconds = (end - start) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            long remainingSeconds = seconds % 60;
            long remainingMinutes = minutes % 60;
            long days = hours / 24;
            long remainingHours = hours % 24;

            System.out.println("The process took: ");
            System.out.printf("%d days, %d hours, %d minutes, %d seconds%n", days, remainingHours, remainingMinutes,
                    remainingSeconds);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}