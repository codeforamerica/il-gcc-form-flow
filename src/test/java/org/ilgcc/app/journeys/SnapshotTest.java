package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.io.IOException;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class SnapshotTest extends AbstractBasePageTest {

    @Test
    void primaryParentJobDetails() throws IOException {
        testPage.navigateToFlowScreen("gcc/parent-info-intro");
        Submission submission = getSessionSubmissionTestBuilder().withDayCareProvider()
            .with("parentFirstName", "firstName")
            .with("companyName", "firstName")
            .with("employerStreetAddress", "lastName")
            .with("employerCity", "city name")
            .with("employerState", "CA")
            .with("employerZipCode", "952654")
            .with("employerPhoneNumber", "(123) 325-34543")
            .build();
        saveSubmission(submission);

        List fieldsToTest = List.of("APPLICANT_EMPLOYER_NAME", "APPLICANT_EMPLOYER_ADDRESS", "APPLICANT_EMPLOYER_CITY", "APPLICANT_EMPLOYER_STATE", "APPLICANT_EMPLOYER_ZIP", "APPLICANT_EMPLOYER_PHONE");
        regenerateExpectedPDFfromSubmission(submission, Thread.currentThread().getStackTrace()[1].getMethodName());
        verifyMatchingFields(generatedPdfFieldsFromSubmission(submission), generateExpectedFields(Thread.currentThread().getStackTrace()[1].getMethodName()), fieldsToTest);

    }
}
