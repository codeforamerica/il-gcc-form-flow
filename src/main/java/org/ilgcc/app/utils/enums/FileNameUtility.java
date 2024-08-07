package org.ilgcc.app.utils.enums;

import formflow.library.data.SubmissionRepositoryService;
import java.text.Normalizer;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FileNameUtility {

    private SubmissionRepositoryService submissionRepositoryService;


//    public String getFileNameForUpload(MultipartFile file, String filePath) {
//        String uuid = filePath.split("/")[0];
//        submissionRepositoryService.findById(UUID.fromString(uuid)).ifPresent(submission -> {
//            String applicantFullName = SubmissionUtilities.applicantFullLegalName(submission.getInputData());
//            String applicantNameForFileName = getApplicantNameForFileName(applicantFullName);
//            OffsetDateTime submittedAt = submission.getSubmittedAt();
//            String centralTimestampFromSubmittedAt = getCentralTimestampFromSubmittedAt(submittedAt);
//
//
//        }
//    }

    public static String getApplicantNameForFileName(String fullName) {
        // Breaks down diacritics into English letter and diacritic seperately
        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD);

        // Remove diacritic marks
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");

        // Remove non-alphanumeric characters except spaces and hyphens
        String cleaned = withoutDiacritics.replaceAll("[^a-zA-Z0-9 \\-]", "");

        // Split the full name into parts splitting on white space
        String[] nameParts = cleaned.split("\\s+");

        // Combine parts with hyphens
        return String.join("-", nameParts);
    }
    
    private String getCentralTimestampFromSubmittedAt(OffsetDateTime submittedAt) {
        ZonedDateTime centralTime = submittedAt.atZoneSameInstant(ZoneId.of("America/Chicago"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return centralTime.format(formatter);
    }
}
