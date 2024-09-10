package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.ilgcc.app.utils.ChildCareProvider;
import org.junit.jupiter.api.Test;
public class DocumentUploadConditionalLogicJourneyTest extends AbstractBasePageTest {
    boolean hasPartner = false;
    @Test
    void shouldSkipDocUploadPromptIfNoDocumentsAreRequiredForParentOrSpouse(){
    testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
    saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
        .withParentDetails()
        .withChild("First", "Child", "Yes")
        .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
        .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
        .with("parentHomeExperiencingHomelessness[]", List.of())
        .with("activitiesParentChildcareReason[]", List.of())
        .with("activitiesParentPartnerChildcareReason[]", List.of())
        .withParentPartnerDetails()
        .build());
    hasPartner = true;
    navigatePassedSignedName(hasPartner);
    // submit-complete
    assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
    testPage.clickButton(getEnMessage("submit-complete-no-documents-to-upload.finish-application"));

    assertThat(testPage.getTitle()).isEqualTo(getEnMessageWithParams("submit-confirmation.title", List.of(ChildCareProvider.OPEN_SESAME.getDisplayName()).toArray()));

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
        hasPartner = false;
        navigatePassedSignedName(hasPartner);

        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));
        // doc-upload-recommended-docs
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
    }

    @Test
    void shouldNotDisplayHomelessnessInstructionsForDocumentUploadIfHomelessnessNotSelected(){
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());
        hasPartner = false;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());
        hasPartner = false;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .build());
        hasPartner = false;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("WORKING"))
            .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
            .build());
        hasPartner = false;
        navigatePassedSignedName(hasPartner);
        // submit-complete
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-complete.title"));
        testPage.clickButton(getEnMessage("submit-complete.button.submit-docs"));

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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("WORKING"))
            .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
            .build());
        hasPartner = false;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
            .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING", "WORKING"))
            .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "false")
            .build());
        hasPartner = true;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
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
        hasPartner = true;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING"))
            .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234", "true")
            .build());
        hasPartner = true;
        navigatePassedSignedName(hasPartner);
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
        testPage.navigateToFlowScreen("gcc/submit-ccap-terms");
        saveSubmission(getSessionSubmissionTestBuilder().withDayCareProvider()
            .withParentDetails()
            .withParentPartnerDetails()
            .withChild("First", "Child", "Yes")
            .with("parentMailingAddressSameAsHomeAddress[]", List.of("no"))
            .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
            .with("parentHomeExperiencingHomelessness[]", List.of())
            .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING", "SCHOOL"))
            .build());
        hasPartner = true;
        navigatePassedSignedName(hasPartner);
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
    void navigatePassedSignedName(boolean hasQualifiedPartner){
        // submit-ccap-terms
        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-ccap-terms.title"));
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        assertThat(testPage.getTitle()).isEqualTo(getEnMessage("submit-sign-name.title"));
        testPage.enter("signedName", "parent first parent last");
        if(hasQualifiedPartner){
            testPage.enter("partnerSignedName", "partner parent");
        }
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));
    }
}
