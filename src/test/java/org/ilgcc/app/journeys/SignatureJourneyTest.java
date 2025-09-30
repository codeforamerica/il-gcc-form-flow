package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = {"il-gcc.enable-multiple-providers=true"})
public class SignatureJourneyTest extends AbstractBasePageTest {
    Map<String, List<String>> childcareScheduleIDs = new HashMap<>();

    @Test
    void signatureForPartnerExistsIfEligible() {
        // CCAP Terms
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

        saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                .withParentPartnerDetails()
                .build()
        );

        testPage.clickElementById("agreesToLegalTerms-true-label");
        testPage.clickContinue();
        //Signature Page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        assertThat(testPage.findElementTextById("signedName")).isNotNull();
        assertThat(testPage.findElementTextById("partnerSignedName")).isNotNull();
    }

    @Test
    void signatureForPartnerDoesNotExistIfIneligible() {
        // CCAP Terms
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                .withParentPartnerDetails()
                .with("parentHasQualifyingPartner", "false")
                .build()
        );

        testPage.clickElementById("agreesToLegalTerms-true-label");
        testPage.clickContinue();
        //Signature Page
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        assertThat(testPage.findElementTextById("signedName")).isNotNull();
        assertThat(testPage.elementDoesNotExistById("partnerSignedName")).isTrue();
    }
}