package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.ilgcc.app.utils.ChildcareReasonOption;
public class DocumentUploadConditionalLogicJourneyTest extends AbstractBasePageTest {
    @Test
    void skipDocUploadRecommendedDocsScreenIfNoDocumentsAreRequiredForParentandNoSpouse(){
    testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
    saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
        .withParentDetails()
        .withChild("First", "Child", "Yes")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("parentHomeExperiencingHomelessness[]", List.of())
        .with("activitiesParentChildcareReason[]", List.of())
        .build());
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
    testPage.clickElementById("agreesToLegalTerms-true");
    testPage.clickContinue();

    // submit-sign-name
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
    testPage.enter("signedName", "parent first parent last");
    testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

    // submit-complete
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
    testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
    }
    @Test
    void skipNotSkipDocUploadRecommendedDocsScreenIfOneDocumentIsRequiredForParent(){
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        // submit-sign-name
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
    }
}
