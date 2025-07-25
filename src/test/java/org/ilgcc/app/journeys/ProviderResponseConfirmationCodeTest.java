package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Slf4j
public class ProviderResponseConfirmationCodeTest extends AbstractBasePageTest {

    Submission familySubmission;

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Nested
    class whenConfirmationCodeIsActive {

        @Test
        void usesAConfirmationCode() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withParentDetails()
                    .with("parentPreferredName", "FirstName")
                    .withChild("First", "Child", "true")
                    .withChild("Second", "Child", "true")
                    .withChild("NoAssistance", "Child", "No")
                    .withConstantChildcareSchedule(0)
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withShortCode("ABC123")
                    .build());

            driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            testPage.enter("providerResponseFamilyShortCode", familySubmission.getShortCode());
            testPage.clickContinue();

            // paid-by-ccap
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
            testPage.clickYes();
        }

        @Test
        void usesALink() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withParentDetails()
                    .with("parentPreferredName", "FirstName")
                    .withChild("First", "Child", "true")
                    .withChild("Second", "Child", "true")
                    .withChild("NoAssistance", "Child", "No")
                    .withConstantChildcareSchedule(0)
                    .withSubmittedAtDate(OffsetDateTime.now())
                    .withShortCode("ABC123")
                    .build());

            driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, familySubmission.getShortCode()));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(
                    familySubmission.getShortCode());
            testPage.clickContinue();

            // paid-by-ccap
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
            testPage.clickYes();
        }
    }

    @Nested
    class whenConfirmationCodeIsInvalid {

        @Test
        void usesAConfirmationCode() {
            driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            testPage.enter("providerResponseFamilyShortCode", "");
            testPage.clickContinue();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
            assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
            testPage.enter("providerResponseFamilyShortCode", "123");
            testPage.clickContinue();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
            assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
            testPage.enter("providerResponseFamilyShortCode", "ABCDEFGHIJKLMNOP");
            testPage.clickContinue();

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
            assertThat(testPage.hasErrorText(getEnMessage("errors.provide-applicant-number"))).isTrue();
        }

        @Test
        void usesALink() {
            driver.navigate().to("http://localhost:%s/s/sdfsjlfjsdf".formatted(localServerPort));

            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("error-invalid-code.title"));
        }

    }

    @Nested
    class whenConfirmationCodeIsInactive {

        @Test
        void whenApplicationHasExpired() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withParentDetails()
                    .with("parentPreferredName", "FirstName")
                    .with("familyIntendedProviderName", "Open Sesame")
                    .withChild("First", "Child", "true")
                    .withChild("Second", "Child", "true")
                    .withChild("NoAssistance", "Child", "No")
                    .withConstantChildcareSchedule(0)
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .withShortCode("ABC123")
                    .build());

            driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            testPage.enter("providerResponseFamilyShortCode", familySubmission.getShortCode());
            testPage.clickContinue();

            // expired
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            assertThat(testPage.getHeader()).isEqualTo(
                    getEnMessageWithParams("provider-response-submit-start.expired.header", new Object[]{"Open Sesame"}));
        }

        @Test
        void familyApplicationAlreadyHasResponse() {
            familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                    .withFlow("gcc")
                    .withParentDetails()
                    .with("parentPreferredName", "FirstName")
                    .with("familyIntendedProviderName", "Dev Provider")
                    .withChild("First", "Child", "true")
                    .withChild("Second", "Child", "true")
                    .withChild("NoAssistance", "Child", "No")
                    .withConstantChildcareSchedule(0)
                    .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                    .withShortCode("ABC123")
                    .with("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name())
                    .build());

            driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

            // submit-start when application is still active
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

            // confirmation-code
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
            testPage.enter("providerResponseFamilyShortCode", familySubmission.getShortCode());
            testPage.clickContinue();

            // submit-start
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
            assertThat(testPage.getHeader()).isEqualTo(
                    getEnMessageWithParams("provider-response-error-response-recorded.header", new Object[]{"Dev Provider"}));
        }

    }

}
