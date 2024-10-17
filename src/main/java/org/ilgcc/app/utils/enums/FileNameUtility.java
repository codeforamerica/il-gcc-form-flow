package org.ilgcc.app.utils.enums;

import static org.ilgcc.app.utils.SubmissionUtilities.getApplicantNameLastToFirst;
import static org.ilgcc.app.utils.SubmissionUtilities.getDashFormattedSubmittedAtDate;
import static org.ilgcc.app.utils.SubmissionUtilities.getDashFormattedSubmittedAtDateWithTime;

import formflow.library.data.Submission;
import java.text.Normalizer;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.SubmissionUtilities;


@Slf4j
public class FileNameUtility {


    public static String getFileNameForPdf(Submission submission) {
        String applicantNameLastToFirst = SubmissionUtilities.getApplicantNameLastToFirst(submission);
        String formattedApplicantName = formatApplicantNameForFileName(applicantNameLastToFirst);
        String dashFormattedSubmittedAtDate = SubmissionUtilities.getDashFormattedSubmittedAtDate(submission);
        return String.format("%s-%s-CCAP-Application-Form.pdf", formattedApplicantName, dashFormattedSubmittedAtDate);
    }

    public static String getFileNameForPdf(Submission submission, String suffix) {
        String applicantNameLastToFirst = SubmissionUtilities.getApplicantNameLastToFirst(submission);
        String formattedApplicantName = formatApplicantNameForFileName(applicantNameLastToFirst);
        String dashFormattedSubmittedAtDate = SubmissionUtilities.getDashFormattedSubmittedAtDate(submission);
        return String.format("%s-%s-CCAP-Application-Form-%s.pdf", formattedApplicantName, dashFormattedSubmittedAtDate, suffix);
    }

    public static String getFileNameForUploadedDocument(Submission submission, int fileNumber, int totalFiles, String fileExtension) {
        String applicantNameLastToFirst = SubmissionUtilities.getApplicantNameLastToFirst(submission);
        String formattedApplicantName = formatApplicantNameForFileName(applicantNameLastToFirst);
        String dashFormattedSubmittedAtDate = SubmissionUtilities.getDashFormattedSubmittedAtDate(submission);
        return String.format("%s-%s-CCAP-Application-Form-Supporting-Document-%d-of-%d.%s",
                formattedApplicantName, dashFormattedSubmittedAtDate, fileNumber, totalFiles, fileExtension);
    }

    public static String formatApplicantNameForFileName(String fullNameLastToFirst) {
        // Breaks down diacritics into English letter and diacritic seperately
        String normalized = Normalizer.normalize(fullNameLastToFirst, Normalizer.Form.NFD);

        // Remove diacritic marks
        String withoutDiacritics = normalized.replaceAll("\\p{M}", "");

        // Remove non-alphanumeric characters except spaces and hyphens
        String cleaned = withoutDiacritics.replaceAll("[^a-zA-Z0-9 \\-]", "");

        // Split the full name into parts splitting on white space
        String[] nameParts = cleaned.split("\\s+");

        // Combine parts with hyphens
        return String.join("-", nameParts);
    }

    public static String getSharePointFilePath(Submission submission, String processingOrg) {
        return String.format("/%s/%s/%s",
                processingOrg,
                getDashFormattedSubmittedAtDate(submission),
                formatApplicantNameForFileName(getApplicantNameLastToFirst(submission) + "-" +
                        getDashFormattedSubmittedAtDateWithTime(submission)));
    }
}
