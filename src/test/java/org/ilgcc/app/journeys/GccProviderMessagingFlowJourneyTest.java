package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-provider-messaging=true"})
public class GccProviderMessagingFlowJourneyTest extends AbstractBasePageTest {

  @Test
  void ProviderMessagingStepsHappyPath() throws Exception {
    testPage.navigateToFlowScreen("gcc/parent-info-disability");

    saveSubmission(getSessionSubmissionTestBuilder()
        .withParentBasicInfo()
        .with("familyIntendedProviderName", "ACME Daycare")
        .with("applicationCounty", "LEE")
        .withShortCode("familyShortCode")
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
    testPage.clickElementById("contactProviderMethod-OTHER-label");
    testPage.clickContinue();

    // submit-contact-provider-email
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-provider-email.title"));
    testPage.enter("familyIntendedProviderEmail", "test@test.com");
    testPage.clickContinue();

    // submit-confirm-provider-email
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirm-provider-email.title"));
    assertThat(testPage.findElementTextById("intended-provider-email")).isEqualTo("test@test.com");
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

    //here we need to test the submit-contact-provider-text
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-provider-text.title"));
    assertThat(testPage.findElementTextById("familyIntendedProviderPhoneNumber")).isEqualTo("");
    testPage.clickContinue();

    assertThat(testPage.hasErrorText(getEnMessage("errors.invalid-phone-number"))).isTrue();
    testPage.enter("familyIntendedProviderPhoneNumber", "(333)333-3333");
    testPage.clickContinue();

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirm-provider-number.title"));
    testPage.clickNo();

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-provider-edit-text.title"));
    testPage.clickContinue();

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-confirm-provider-number.title"));
    testPage.clickYes();

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-contact-provider-text-confirmation.title"));
    testPage.clickContinue();

    // submit-confirm-provider-number
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-share-confirmation-code.title"));
    testPage.clickButton(getEnMessage("submit-share-confirmation-code.cta"));

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
  }
}