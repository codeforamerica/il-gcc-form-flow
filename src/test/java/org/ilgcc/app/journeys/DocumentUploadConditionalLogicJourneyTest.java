package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;

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
        .with("activitiesParentChildcareReason", List.of())
        .build());
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
//    testPage.clickElementById();
    }
}
