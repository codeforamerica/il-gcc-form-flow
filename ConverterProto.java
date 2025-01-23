import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class ConverterProto {
//    public static void main(String[] args) {
//        String pngFilePath = "/Users/mperlman@codeforamerica.org/Downloads/PXL_20250120_230431182.gif";
//        String pdfFilePath = "/Users/mperlman@codeforamerica.org/Downloads/output.pdf";    // Output PDF file path
//
//        try {
//            // Create a new document
//            Document document = new Document();
//            PdfWriter.getInstance(document, new FileOutputStream(pdfFilePath));
//
//            // Open the document
//            document.open();
//
//            // Add the image to the document
//            Image image = Image.getInstance(pngFilePath);
//
//            // Scale the image to fit the page size if needed
//            image.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());
//            image.setAlignment(Image.ALIGN_CENTER);
//
//            // Add image to PDF
//            document.add(image);
//
//            // Close the document
//            document.close();
//
//            System.out.println("PNG file converted to PDF successfully: " + pdfFilePath);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        String docFilePath = "/Users/mperlman@codeforamerica.org/Downloads/sample.doc"; // Path to the .doc file
        String outputDir = "/Users/mperlman@codeforamerica.org/Downloads/sampleoutput/";   // Output directory for the PDF

        // Ensure the output directory exists
        File outputDirectory = new File(outputDir);
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        // Build the LibreOffice command
        String[] command = {
                "/opt/homebrew/bin/soffice", // LibreOffice executable
                "--headless", // Run in headless mode
                "--safe-mode",
                "--convert-to", "pdf", // Conversion format
                docFilePath // Input file
        };

        try {
            // Use ProcessBuilder to execute the command
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.environment().putAll(System.getenv()); // Inherit parent environment
//            processBuilder.redirectErrorStream(true); // Merge stderr with stdout

            processBuilder.redirectError(new File("error.log"));
            processBuilder.redirectOutput(new File("output.log"));

            System.out.println(String.join(" ", command));

            System.out.println("Starting process...");
            // Start the process
            long start = new Date().getTime();

            Process process = processBuilder.start();

            // Handle stdout
//            Thread outputThread = new Thread(() -> {
//                try (BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(process.getInputStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        System.out.println("OUTPUT: " + line);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // Handle stderr
//            Thread errorThread = new Thread(() -> {
//                try (BufferedReader reader = new BufferedReader(
//                        new InputStreamReader(process.getErrorStream()))) {
//                    String line;
//                    while ((line = reader.readLine()) != null) {
//                        System.err.println("ERROR: " + line);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
//
//            // Start threads
//            outputThread.start();
//            errorThread.start();

            // Wait for the process to complete
            System.out.println("Waiting...");
            int exitCode = process.waitFor();
            long end = new Date().getTime();

            long seconds = (end-start) / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;

            long remainingSeconds = seconds % 60;
            long remainingMinutes = minutes % 60;
            long days = hours / 24;
            long remainingHours = hours % 24;

            System.out.println("The process took: ");
            System.out.printf("%d days, %d hours, %d minutes, %d seconds%n",
                    days, remainingHours, remainingMinutes, remainingSeconds);

//processBuilder.redirectError(new File("error.log"));
//processBuilder.redirectOutput(new File("output.log"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
