package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_APPROVED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_DENIED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_STATUS_C_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_STATUS_R_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.CURRENT_WITHDRAWN_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.OUTDATED_APPROVED_PROVIDER;
import static org.ilgcc.app.data.importer.FakeProviderDataImporter.OUTDATED_PENDING_PROVIDER;

import formflow.library.data.Submission;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@Slf4j
public class ProviderresponseFlowJourneyTest extends AbstractBasePageTest {

    @AfterEach
    protected void clearSubmissions() {
        super.clearSubmissions();
    }

    @Test
    void ProviderresponseJourneyTest_validLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1),
                        List.of(programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .withShortCode("ABC123")
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s/%s".formatted(localServerPort, s.getShortCode()));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        assertThat(testPage.findElementById("providerResponseFamilyShortCode").getAttribute("value")).isEqualTo(s.getShortCode());
        testPage.clickContinue();

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickYes();

        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
        testPage.enter("providerResponseBusinessName", "Business Name");
        testPage.enter("providerResponseFirstName", "First Name");
        testPage.enter("providerResponseLastName", "Last Name");
        testPage.clickButton("Continue");

        // service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-service-address.title"));
        testPage.enter("providerResponseServiceStreetAddress1", "123 Main St");
        testPage.enter("providerResponseServiceCity", "City");
        testPage.selectFromDropdown("providerResponseServiceState", "IL - Illinois");
        testPage.enter("providerResponseServiceZipCode", "12345");
        testPage.clickButton("Continue");

        // confirm-service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        testPage.clickButton("Use this address");

        // contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickButton("Continue");

        // info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        assertThat(testPage.findElementTextById("business-name")).isEqualTo("Business Name");
        assertThat(testPage.findElementTextById("full-name")).isEqualTo("First Name Last Name");
        assertThat(testPage.findElementTextById("provider-service-street-address-1")).isEqualTo("123 Main St");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("City, IL");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("12345");
        assertThat(testPage.findElementTextById("phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("email")).isEqualTo("foo@bar.com");
        testPage.clickButton("Continue");
        
        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", CURRENT_APPROVED_PROVIDER.getProviderId().toString());
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        assertThat(testPage.findElementTextById("confirmation-code")).contains(s.getShortCode());
        assertThat(testPage.findElementTextById("parent-name")).contains("parent first parent last");

        assertThat(testPage.findElementTextById("child-name-0")).contains("childFirst childLast");

        assertThat(testPage.elementDoesNotExistById("child-name-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

        testPage.clickElementById("providerResponseAgreeToCare-true-label");
        testPage.clickButton("Submit");

        //submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.title"));
        // Existing provider who agreed to care should see this header
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-confirmation.header"));
        // If they are an existing provider they should see the survey legend that says how was the response experience
        WebElement surveyLegend = driver.findElement(By.xpath("//legend[@id='providerSurveyProviderDifficulty-legend']/span"));
        assertThat(surveyLegend.getText()).isEqualTo(getEnMessage("submit-confirmation.existing-provider.experience-question"));
        // We should see the notice about contacting CCR&R because they are an existing provider who agreed to care
        List<WebElement> notices = driver.findElements(By.cssSelector(".notice.notice--gray"));
        assertThat(notices).isNotEmpty();
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.title"));
    }

    @Test
    void ProviderresponseJourneyTest_noLink() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1),
                        List.of(programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .withShortCode("ABC123")
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", s.getShortCode());
        testPage.clickContinue();

        // paid-by-ccap
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("paid-by-ccap.title"));
        testPage.clickYes();

        //provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info.title"));
        testPage.enter("providerResponseBusinessName", "Business Name");
        testPage.enter("providerResponseFirstName", "First Name");
        testPage.enter("providerResponseLastName", "Last Name");
        testPage.clickButton("Continue");

        // service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-service-address.title"));
        testPage.enter("providerResponseServiceStreetAddress1", "123 Main St");
        testPage.enter("providerResponseServiceCity", "City");
        testPage.selectFromDropdown("providerResponseServiceState", "IL - Illinois");
        testPage.enter("providerResponseServiceZipCode", "12345");
        testPage.clickButton("Continue");

        // confirm-service-address
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("confirm-address.title"));
        testPage.clickButton("Use this address");

        // contact-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-contact-info.title"));
        testPage.enter("providerResponseContactPhoneNumber", "5555555555");
        testPage.enter("providerResponseContactEmail", "foo@bar.com");
        testPage.clickButton("Continue");

        // info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-info-review.title"));
        assertThat(testPage.findElementTextById("business-name")).isEqualTo("Business Name");
        assertThat(testPage.findElementTextById("full-name")).isEqualTo("First Name Last Name");
        assertThat(testPage.findElementTextById("provider-service-street-address-1")).isEqualTo("123 Main St");
        assertThat(testPage.findElementTextById("provider-service-city-state")).isEqualTo("City, IL");
        assertThat(testPage.findElementTextById("provider-service-zipcode")).isEqualTo("12345");
        assertThat(testPage.findElementTextById("phone")).isEqualTo("(555) 555-5555");
        assertThat(testPage.findElementTextById("email")).isEqualTo("foo@bar.com");
        testPage.clickButton("Continue");

        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", CURRENT_APPROVED_PROVIDER.getProviderId().toString());
        testPage.clickContinue();

        // response
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-response.title"));
        assertThat(testPage.findElementTextById("confirmation-code")).contains(s.getShortCode());
        assertThat(testPage.findElementTextById("parent-name")).contains("parent first parent last");

        assertThat(testPage.findElementTextById("child-name-0")).contains("childFirst childLast");

        assertThat(testPage.elementDoesNotExistById("child-name-1")).isTrue();
        assertThat(testPage.elementDoesNotExistById("child-name-2")).isTrue();

        testPage.clickElementById("providerResponseAgreeToCare-true-label");
        testPage.clickButton("Submit");

        //submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.title"));
        testPage.goBack();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirmation.title"));
    }

    @Test
    void ProviderresponseJourneyTest_noLink_invalidConfirmationCode() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1, child_2, child_3),
                        List.of(programProvider, programProvider, programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .withShortCode("ABC123")
                .build());

        testPage.clickContinue();

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
        testPage.enter("providerResponseFamilyShortCode", s.getShortCode());
        testPage.clickContinue();
    }

    @Test
    void ProviderresponseJourneyTest_noLink_expiredConfirmationCode() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1, child_2, child_3),
                        List.of(programProvider, programProvider, programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .with("providerApplicationResponseStatus", SubmissionStatus.EXPIRED.name())
                .withShortCode("ABC123")
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", s.getShortCode());
        testPage.clickContinue();

        // expired
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("provider-response-submit-start.expired.header", new Object[]{"child care provider"}));
    }

    @Test
    void ProviderresponseJourneyTest_noLink_alreadyResponded() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1, child_2, child_3),
                        List.of(programProvider, programProvider, programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .with("providerApplicationResponseStatus", SubmissionStatus.RESPONDED.name())
                .withShortCode("ABC123")
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", s.getShortCode());
        testPage.clickContinue();

        // submit-start
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        assertThat(testPage.getHeader()).isEqualTo(
                getEnMessageWithParams("provider-response-error-response-recorded.header", new Object[]{"child care provider"}));
    }

    @Test
    void ProviderresponseJourneyTest_ProviderNumberValidation() {
        testPage.navigateToFlowScreen("gcc/activities-parent-intro");

        Submission s = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1, child_2, child_3),
                        List.of(programProvider, programProvider, programProvider))
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .withShortCode("ABC123")
                .build());

        testPage.clickContinue();

        driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", s.getShortCode());
        
        testPage.navigateToFlowScreen("providerresponse/provider-number");
        // provider-number
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-provider-number.title"));
        testPage.enter("providerResponseProviderNumber", CURRENT_STATUS_R_PROVIDER.getProviderId().toString());
        testPage.clickContinue();

        // Errors
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Status = D
        testPage.enter("providerResponseProviderNumber", CURRENT_DENIED_PROVIDER.getProviderId().toString());
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Status = W
        testPage.enter("providerResponseProviderNumber", CURRENT_WITHDRAWN_PROVIDER.getProviderId().toString());
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Status = C
        testPage.enter("providerResponseProviderNumber", CURRENT_STATUS_C_PROVIDER.getProviderId().toString());
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Status = A, 4 years old
        testPage.enter("providerResponseProviderNumber", OUTDATED_APPROVED_PROVIDER.getProviderId().toString());
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Status = P, 5 years old
        testPage.enter("providerResponseProviderNumber", OUTDATED_PENDING_PROVIDER.getProviderId().toString());
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Doesn't exist at all
        testPage.enter("providerResponseProviderNumber", "1234567891234");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("provider-response-provider-number.error.invalid-number"))).isTrue();

        // Too short
        testPage.enter("providerResponseProviderNumber", "12345");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-provider-number-length"))).isTrue();

        // Too Long
        testPage.enter("providerResponseProviderNumber", "12345123451234512345");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-provider-number-length"))).isTrue();

        // Nothing
        testPage.enter("providerResponseProviderNumber", " ");
        testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("errors.general-title"));
        assertThat(testPage.hasErrorText(getEnMessage("errors.provide-provider-number"))).isTrue();
    }

    @Test
    void ProviderresponseJourneyTest_ConfirmationCodeLinkInvalid() {
        driver.navigate().to("http://localhost:%s/s/sdfsjlfjsdf".formatted(localServerPort));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("error-invalid-code.title"));
    }
}
