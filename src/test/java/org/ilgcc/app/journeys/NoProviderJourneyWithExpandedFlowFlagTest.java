package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.ChildcareReasonOption;
import org.ilgcc.app.utils.CountyOption;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {
        "WAIT_FOR_PROVIDER_RESPONSE_FLAG=true",
        "SHOW_NO_PROVIDER_FLOW=true"
})
public class NoProviderJourneyWithExpandedFlowFlagTest extends AbstractBasePageTest {
    @Test
    void WhenApplicantSelectsNoProvider() {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickNo();

        // onboarding-no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("onboarding-no-provider-intro.continue"));

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));

        testPage.navigateToFlowScreen("gcc/submit-intro");
        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.button.do-this-later"));

        // no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("no-provider-intro.title"));
        testPage.clickContinue();

        // no-provider-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("no-provider-notice.title"));
        testPage.clickContinue();

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1).toString()).containsIgnoringCase("4-C: Community Coordinated Child Care");
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));
    }

    @Test
    void WhenApplicantSelectsNoProviderAndHasRequiredDocuments() {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentDetails()
                .withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false")
                .with("activitiesParentChildcareReason[]", List.of(ChildcareReasonOption.WORKING.toString()))
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickNo();

        // onboarding-no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("onboarding-no-provider-intro.continue"));

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));

        testPage.navigateToFlowScreen("gcc/submit-intro");
        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.skip"));

        // no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("no-provider-intro.title"));
        testPage.clickContinue();

        // no-provider-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("no-provider-notice.title"));
        testPage.clickContinue();

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1).toString()).containsIgnoringCase("4-C: Community Coordinated Child Care");
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));
    }

    @Test
    void WhenApplicantSelectsNoProviderAndAddsDocuments() {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentDetails()
                .withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false")
                .with("activitiesParentChildcareReason[]", List.of(ChildcareReasonOption.WORKING.toString()))
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickNo();

        // onboarding-no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-no-provider-intro.title"));
        testPage.clickButton(getEnMessage("onboarding-no-provider-intro.continue"));

        // parent-info-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("parent-info-intro.title"));

        testPage.navigateToFlowScreen("gcc/submit-intro");
        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // doc-upload-recommended docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isTrue();
        uploadJpgFile();
        // The submit button is hidden unless a file has been uploaded. The await gives the system time to remove the "display-none" class.
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none"))
        );

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));

        // Done with adding documents
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));

        // no-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("no-provider-intro.title"));
        testPage.clickContinue();

        // no-provider-notice
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("no-provider-notice.title"));
        testPage.clickContinue();

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1).toString()).containsIgnoringCase("4-C: Community Coordinated Child Care");
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));
    }

    @Test
    void WhenApplicantSelectsAProvider() {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentDetails()
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickYes();

        // onboarding-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info.title"));
        testPage.enterInputById("familyIntendedProviderName", "Intended Provider Name");
        testPage.clickContinue();

        // onboarding-provider-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-review.title"));
        testPage.clickContinue();

        // onboarding-provider-info-confirm
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-confirm.title"));
        testPage.clickContinue();

        testPage.navigateToFlowScreen("gcc/submit-intro");
        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.button.do-this-later"));

        // contact-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-intro.title"));
        testPage.clickContinue();

        // contact-provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-info.title"));
        testPage.clickContinue();

        // contact-provider-message
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-message.title"));
        testPage.clickButton(getEnMessage("contact-provider-message.copy-message"));
        testPage.clickContinue();

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1).toString()).containsIgnoringCase("4-C: Community Coordinated Child Care");
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));
    }

    @Test
    void WhenApplicantSelectsAProviderAndHasRequiredDocuments() {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentDetails()
                .withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false")
                .with("activitiesParentChildcareReason[]", List.of(ChildcareReasonOption.WORKING.toString()))
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickYes();

        // onboarding-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info.title"));
        testPage.enterInputById("familyIntendedProviderName", "Intended Provider Name");
        testPage.clickContinue();

        // onboarding-provider-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-review.title"));
        testPage.clickContinue();

        // onboarding-provider-info-confirm
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-confirm.title"));
        testPage.clickContinue();

        testPage.navigateToFlowScreen("gcc/submit-intro");
        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();

        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.skip"));

        // contact-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-intro.title"));
        testPage.clickContinue();

        // contact-provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-info.title"));
        testPage.clickContinue();

        // contact-provider-message
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-message.title"));
        testPage.clickButton(getEnMessage("contact-provider-message.copy-message"));
        testPage.clickContinue();

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1).toString()).containsIgnoringCase("4-C: Community Coordinated Child Care");
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));
    }

    @Test
    void WhenApplicantSelectsAProviderAndAddsDocuments() {
        // Home page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("index.title"));
        testPage.clickButton(getEnMessage("index.apply-now"));
        // onboarding-getting-started
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-getting-started.title"));
        testPage.clickContinue();

        //onboarding-language-preference
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-language-pref.title"));
        testPage.selectFromDropdown("languageRead", "English");
        testPage.selectFromDropdown("languageSpeak", "Español");
        testPage.clickContinue();

        // onboarding-county
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-county.title"));
        testPage.selectFromDropdown("applicationCounty", CountyOption.LEE.getLabel());

        saveSubmission(getSessionSubmissionTestBuilder()
                .withParentDetails()
                .withJob("jobs", "Regular Schedule Job", "123 Main Str", "", "", "", "", "false")
                .with("activitiesParentChildcareReason[]", List.of(ChildcareReasonOption.WORKING.toString()))
                .withChild("First", "Child", "Yes")
                .withChild("Second", "Child", "Yes")
                .build());
        testPage.clickContinue();

        // onboarding-chosen-provider
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-chosen-provider.title"));
        testPage.clickYes();

        // onboarding-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info.title"));
        testPage.enterInputById("familyIntendedProviderName", "Intended Provider Name");
        testPage.clickContinue();

        // onboarding-provider-info-review
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-review.title"));
        testPage.clickContinue();

        // onboarding-provider-info-confirm
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("onboarding-provider-info-confirm.title"));
        testPage.clickContinue();

        testPage.navigateToFlowScreen("gcc/submit-intro");
        // submit-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-intro.title"));
        testPage.clickContinue();
        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.yes-add-document-now"));

        // doc-upload-recommended docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-add-files
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none")).isTrue();
        uploadJpgFile();
        // The submit button is hidden unless a file has been uploaded. The await gives the system time to remove the "display-none" class.
        await().atMost(5, TimeUnit.SECONDS).until(
                () -> !(testPage.findElementById("form-submit-button").getAttribute("class").contains("display-none"))
        );

        testPage.clickButton(getEnMessage("doc-upload-add-files.confirmation"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-submit-confirmation.title"));

        // Done with adding documents
        testPage.clickButton(getEnMessage("doc-upload-submit-confirmation.yes"));

        // contact-provider-intro
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-intro.title"));
        testPage.clickContinue();

        // contact-provider-info
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-info.title"));
        testPage.clickContinue();

        // contact-provider-message
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("contact-provider-message.title"));
        testPage.clickButton(getEnMessage("contact-provider-message.copy-message"));
        testPage.clickContinue();

        // submit-next-steps
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-next-steps.title"));
        assertThat(testPage.getTextBySelector("ul").get(1).toString()).containsIgnoringCase("4-C: Community Coordinated Child Care");
        testPage.clickContinue();

        // complete-submit-confirmation
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("complete-submit-confirmation.title"));
    }
}