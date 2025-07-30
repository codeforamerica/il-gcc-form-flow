package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class GenerateShortLinkAndStoreProviderApplicationStatusTest {

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    HttpServletRequest httpRequest;

    GenerateShortLinkAndStoreProviderApplicationStatus generateShortLinkAndStoreProviderApplicationStatus;

    Submission familySubmission;

    Map<String, Object> provider1 = new HashMap<>();
    Map<String, Object> provider2 = new HashMap<>();
    OffsetDateTime submittedAtDate = OffsetDateTime.of(2025, 04, 28, 7, 0, 0, 0, ZoneOffset.UTC);

    OffsetDateTime threeDaysAfterSubmittedAtDate = submittedAtDate.plusDays(3);

    @Nested
    class whenEnableMultipleProvidersFlow {

        @BeforeEach
        void setUp() {

            provider1.put("uuid", UUID.randomUUID().toString());
            provider1.put("iterationIsComplete", true);
            provider1.put("providerFirstName", "FirstName");
            provider1.put("providerLastName", "LastName");
            provider1.put("familyIntendedProviderEmail", "firstLast@mail.com");
            provider1.put("familyIntendedProviderPhoneNumber", "(999) 123-1234");
            provider1.put("familyIntendedProviderAddress", "123 Main St.");
            provider1.put("familyIntendedProviderCity", "Chicago");
            provider1.put("familyIntendedProviderState", "IL");
            provider1.put("providerType", "Individual");

            provider2.put("uuid", UUID.randomUUID().toString());
            provider2.put("iterationIsComplete", true);
            provider2.put("childCareProgramName", "Child Care Program Name");
            provider2.put("familyIntendedProviderEmail", "ccpn@mail.com");
            provider2.put("familyIntendedProviderPhoneNumber", "(123) 123-1234");
            provider2.put("familyIntendedProviderAddress", "456 Main St.");
            provider2.put("familyIntendedProviderCity", "Chicago");
            provider2.put("familyIntendedProviderState", "IL");
            provider2.put("providerType", "Care Program");

            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(submittedAtDate)
                    .build());
        }

        @Test
        void setsExpirationDate3DaysIntoFutureWhenProviderIsChosen() {
            familySubmission.getInputData().put("providers", List.of(provider1, provider2));
            submissionRepositoryService.save(familySubmission);

            generateShortLinkAndStoreProviderApplicationStatus = new GenerateShortLinkAndStoreProviderApplicationStatus(
                    httpRequest, false, 0);
            generateShortLinkAndStoreProviderApplicationStatus.run(familySubmission);

            assertThat(familySubmission.getInputData().get("providerApplicationResponseStatus")).isEqualTo(
                    "ACTIVE");
            assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                    threeDaysAfterSubmittedAtDate.atZoneSameInstant(ZoneId.of("America/Chicago")));
        }

        @Test
        void setsExpirationDateToNowWhenNoProviderIsChosen() {
            familySubmission.getInputData().put("providers", List.of());
            submissionRepositoryService.save(familySubmission);

            generateShortLinkAndStoreProviderApplicationStatus = new GenerateShortLinkAndStoreProviderApplicationStatus(
                    httpRequest, false, 0);
            generateShortLinkAndStoreProviderApplicationStatus.run(familySubmission);

            assertThat(familySubmission.getInputData().get("providerApplicationResponseStatus")).isEqualTo(
                    "INACTIVE");
            assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                    submittedAtDate.atZoneSameInstant(
                            ZoneId.of("America/Chicago")));
        }
    }

    @Nested
    class whenSingleProviderFlow {

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(submittedAtDate)
                    .build());

            generateShortLinkAndStoreProviderApplicationStatus = new GenerateShortLinkAndStoreProviderApplicationStatus(
                    httpRequest, false, 0);
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
                    "INACTIVE");
            assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                    submittedAtDate.atZoneSameInstant(
                            ZoneId.of("America/Chicago")));
        }
    }

    @Nested
    class whenEnableFasterExpirationIsTrue {

        @BeforeEach
        void setUp() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withSubmittedAtDate(submittedAtDate)
                    .build());

            generateShortLinkAndStoreProviderApplicationStatus = new GenerateShortLinkAndStoreProviderApplicationStatus(
                    httpRequest, true, 120);
        }

        @Test
        void setsExpirationDate2HoursIntoFutureWhenProviderIsChosen() {
            familySubmission.getInputData().put("hasChosenProvider", "true");
            submissionRepositoryService.save(familySubmission);

            generateShortLinkAndStoreProviderApplicationStatus.run(familySubmission);

            assertThat(familySubmission.getInputData().get("providerApplicationResponseStatus")).isEqualTo(
                    "ACTIVE");
            assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                    familySubmission.getSubmittedAt().plusHours(2).atZoneSameInstant(ZoneId.of("America/Chicago")));
        }

        @Test
        void setsExpirationDateToNowWhenNoProviderIsChosen() {
            familySubmission.getInputData().put("hasChosenProvider", "false");
            submissionRepositoryService.save(familySubmission);

            generateShortLinkAndStoreProviderApplicationStatus.run(familySubmission);

            assertThat(familySubmission.getInputData().get("providerApplicationResponseStatus")).isEqualTo(
                    "INACTIVE");
            assertThat(familySubmission.getInputData().get("providerApplicationResponseExpirationDate")).isEqualTo(
                    submittedAtDate.atZoneSameInstant(
                            ZoneId.of("America/Chicago")));
        }
    }
}