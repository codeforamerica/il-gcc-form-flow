package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Test;
public class DocumentUploadConditionalLogicJourneyTest extends AbstractBasePageTest {
    @Test
    void skipDocUploadRecommendedDocsScreenIfNoDocumentsAreRequiredForParentandNoSpouse(){
    testPage.navigateToFlowScreen("gcc/submit-complete");
    saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
        .withParentDetails()
        .withChild("First", "Child", "Yes")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("parentHomeExperiencingHomelessness[]", List.of())
        .with("activitiesParentChildcareReason[]", List.of())
        .build());

    // submit-complete
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
    testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
    assertThat(testPage.elementDoesNotExistById("controlId")).isTrue();
    }
    @Test
    void skipNotSkipDocUploadRecommendedDocsScreenIfOneDocumentIsRequiredForParent(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
    }

    @Test
    void shouldNotDisplayHomelessnessInstructionsForDocumentUploadIfHomelessnessNotSelected(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("tanf-training-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.training.body"));
        assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isFalse();
        assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
    }

    @Test
    void shouldNotDisplaySchoolInstructionsForDocumentUploadIfSchoolNotSelected(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("tanf-training-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.training.body"));
        assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isFalse();
        assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
    }
    @Test
    void shouldNotDisplayJobInstructionsForDocumentUploadIfJobIsNotSelected(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("tanf-training-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.training.body"));
        assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
    }
    @Test
    void shouldNotDisplaySelfEmploymentInstructionsForDocumentUploadIfSelfEmploymentIsNotSelected(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("WORKING"))
            .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("job-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.jobs.body"));
        assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
    }
    @Test
    void shouldNotDisplayTanfInstructionsForDocumentUploadIfTanfIsNotSelected(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("WORKING"))
            .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("job-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.jobs.body"));
        assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
    }
    @Test
    void shouldDisplayJobsInstructionsIfPartnerSelectsJobsAndParentDoesNot(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .with("parentHasPartner", "true")
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING", "WORKING"))
            .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("tanf-training-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.training.body"));
        assertThat(testPage.elementDoesNotExistById("job-recommendation")).isFalse();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isFalse();
    }
    @Test
    void shouldDisplaySelfEmploymentInstructionsIfPartnerSelectsSelfEmploymentAndParentDoesNot(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "true")
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("tanf-training-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.training.body"));
        assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isFalse();

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
    }

    @Test
    void shouldDisplayTanfInstructionsIfPartnerSelectsTanfAndParentDoesNot(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .with("parentHasPartner", "true")
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING"))
            .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "true")
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("tanf-training-recommendation")).contains(getEnMessage("doc-upload-recommended-docs.training.body"));

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isFalse();
    }
    @Test
    void shouldDisplaySchoolInstructionsIfPartnerSelectsSchoolAndParentDoesNot(){
        testPage.navigateToFlowScreen("gcc/submit-complete");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .with("parentHasPartner", "true")
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING", "SCHOOL"))
            .build());

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
        assertThat(testPage.getElementText("education-documentation")).contains(getEnMessage("doc-upload-recommended-docs.school.body"));

        // doc-upload-add-files
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
        assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isFalse();
    }
}
