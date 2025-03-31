package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;
@TestPropertySource(properties = {"il-gcc.enable-provider-messaging=true"})
public class GccProviderMessagingFlowJourneyTest extends AbstractBasePageTest {

    @Test
    void ProviderMessagingSteps() {
        testPage.navigateToFlowScreen("gcc/parent-info-disability");

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentBasicInfo()
                .with("familyIntendedProviderName", "ACME Daycare")
                .with("applicationCounty", "LEE")
                .build());

        testPage.clickYes();

        testPage.navigateToFlowScreen("gcc/submit-intro");

        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        assertThat(testPage.getHeader()).isEqualTo(getEnMessage("submit-intro.header.contact-provider"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-contact-method
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-method.title"));
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.submit-contact-method"))).isTrue();
        testPage.clickElementById("contactProviderMethod-EMAIL-label");
        testPage.clickElementById("contactProviderMethod-TEXT-label");
        testPage.clickContinue();

        // submit-contact-provider-email
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-provider-email.title"));
        testPage.clickContinue();

        // submit-confirm-provider-email
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirm-provider-email.title"));
        testPage.clickYes();

        // submit-contact-provider-email-confirmation
        testPage.goBack();
        testPage.clickNo();

        // submit-edit-provider-email
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-edit-provider-email.title"));
        assertThat(testPage.getInputValue("familyIntendedProviderName")).isEqualTo("ACME Daycare");
        assertThat(testPage.findElementById("familyIntendedProviderName").isEnabled()).isFalse();
        assertThat(testPage.findElementById("familyIntendedProviderEmail").isEnabled()).isTrue();
        assertThat(testPage.findElementById("familyIntendedProviderPhoneNumber").isEnabled()).isFalse();

        testPage.enter("familyIntendedProviderEmail", "test");
        testPage.clickContinue();

        assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-email"))).isTrue();

        testPage.enter("familyIntendedProviderEmail", "test@mail.com");
        testPage.clickContinue();

        // submit-confirm-provider-email
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirm-provider-email.title"));
        testPage.clickYes();
        
        // submit-contact-provider-email-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-provider-email-confirmation.title"));
        testPage.clickContinue();

      // submit-contact-provider-email-confirmation
      testPage.navigateToFlowScreen("gcc/submit-contact-provider-email-confirmation");
      assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-provider-email-confirmation.title"));
      testPage.clickContinue();
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirm-provider-email.title"));
        testPage.clickYes();
        
        // parent-confirm-provider-number
        testPage.navigateToFlowScreen("gcc/parent-confirm-provider-number");
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-confirm-provider-number.title"));
        testPage.clickYes();

      // submit-share-confirmation-code
      testPage.navigateToFlowScreen("gcc/submit-share-confirmation-code");
        
      // skips screen and goes to doc-upload-recommended-docs
      assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));

      testPage.navigateToFlowScreen("gcc/submit-contact-method");
      testPage.clickElementById("contactProviderMethod-OTHER-label");
      testPage.clickContinue();

      testPage.navigateToFlowScreen("gcc/submit-share-confirmation-code");
      assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-share-confirmation-code.title"));

      testPage.clickLink(getEnMessage("submit-share-confirmation-code.cta"));

      // doc-upload-recommended-docs
      assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
    }
}