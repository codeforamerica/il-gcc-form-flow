package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

public class SignatureJourneyTest extends AbstractBasePageTest {

    @Test
    void signatureForPartnerExistsIfEligible() {
        // CCAP Terms
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .withChild("Second", "Child", "Yes")
            .build());

        testPage.clickElementById("agreesToLegalTerms-true-label");
        testPage.clickContinue();
        //Signature Page
        assertThat(testPage.getTitle()).isEqualTo("Sign Application");
        assertThat(testPage.findElementTextById("signedName")).isNotNull();
        assertThat(testPage.findElementTextById("partnerSignedName")).isNotNull();
    }

    @Test
    void signatureForPartnerDoesNotExistIfIneligible() {
        // CCAP Terms
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .withChild("Second", "Child", "Yes")
            .with("parentHasQualifyingPartner", "False")
            .build());

        testPage.clickElementById("agreesToLegalTerms-true-label");
        testPage.clickContinue();
        //Signature Page
        assertThat(testPage.getTitle()).isEqualTo("Sign Application");
        assertThat(testPage.findElementTextById("signedName")).isNotNull();
        assertThat(testPage.elementDoesNotExistById("partnerSignedName")).isTrue();
    }
}