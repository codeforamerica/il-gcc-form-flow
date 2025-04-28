package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GenerateShortLinkAndStoreProviderApplicationStatusTest {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    GenerateShortLinkAndStoreProviderApplicationStatus generateShortLinkAndStoreProviderApplicationStatus;

    Submission familySubmission;

    OffsetDateTime submittedAtDate = OffsetDateTime.of(2025, 04, 28, 7, 0, 0, 0, ZoneOffset.UTC);

    OffsetDateTime threeDaysAfterSubmittedAtDate = submittedAtDate.plusDays(3);

    @BeforeEach
    void setUp() {
        familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(submittedAtDate)
                .build());
    }

    @Test
    void setsExpirationDate3DaysIntoFutureWhenProviderIsChosen() {
        familySubmission.getInputData().put("hasChosenProvider", "true");
        submissionRepositoryService.save(familySubmission);

        generateShortLinkAndStoreProviderApplicationStatus.run(familySubmission);

        assertThat(familySubmission.getInputData().get("providerApplicationResponseStatus")).isEqualTo(
                "ACTIVE");
        assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                threeDaysAfterSubmittedAtDate.atZoneSameInstant(ZoneId.of("America/Chicago")));
    }

    @Test
    void setsExpirationDateToNowWhenNoProviderIsChosen() {
        familySubmission.getInputData().put("hasChosenProvider", "false");
        submissionRepositoryService.save(familySubmission);

        generateShortLinkAndStoreProviderApplicationStatus.run(familySubmission);

        assertThat(familySubmission.getInputData().get("providerApplicationResponseStatus")).isEqualTo(
                "ACTIVE");
        assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                submittedAtDate.atZoneSameInstant(
                        ZoneId.of("America/Chicago")));
    }
}