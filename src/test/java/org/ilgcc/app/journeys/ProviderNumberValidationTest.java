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
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
public class ProviderNumberValidationTest extends AbstractBasePageTest {

    @Test
    void ProviderNumberValidationTest() {
        Submission familySubmission = submissionRepositoryService.save(new SubmissionTestBuilder()
                .withFlow("gcc")
                .withParentDetails()
                .with("parentPreferredName", "FirstName")
                .withChild("First", "Child", "true")
                .withChild("Second", "Child", "true")
                .withChild("NoAssistance", "Child", "No")
                .withConstantChildcareSchedule(0)
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(2))
                .withShortCode("ABC123")
                .build());

        driver.navigate().to("http://localhost:%s/s".formatted(localServerPort));

        // submit-start when application is still active
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-submit-start.title"));
        testPage.clickButton(getEnMessage("provider-response-submit-start.active.button"));

        // confirmation-code
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("provider-response-confirmation-code.title"));
        testPage.enter("providerResponseFamilyShortCode", familySubmission.getShortCode());

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

}
