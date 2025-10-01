package org.ilgcc.app.journeys;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.ilgcc.app.utils.AbstractBasePageTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class DocumentUploadConditionalLogicJourneyTest extends AbstractBasePageTest {

    @Nested
    class whenParentHasReasonForChildcare {

        boolean hasPartner = false;

        @Test
        void skipsRecommendedDocumentsScreenIfNoneAreRequired() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of())
                    .with("activitiesParentPartnerChildcareReason[]", List.of())
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("controlId")).isTrue();
        }

        @Test
        void displaysJobsInstructionsWhenParentSelectsJob() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of("WORKING"))
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234",
                            "false")
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displaySelfEmploymentInstructionsWhenParentSelectsSelfEmployment() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of("WORKING"))
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .withJob("jobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234",
                            "true")
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displaysTanfInstructionsWhenParentSelectsTanf() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .with("activitiesParentChildcareReason[]", List.of("TANF_TRAINING"))
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displaySchoolInstructionsWhenPartnerSelectsSchool() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of("SCHOOL"))
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displayHomelessInstructionsWhenPartnerSelectsHomeless() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .with("parentHomeExperiencingHomelessness[]", List.of("yes"))
                    .with("activitiesParentChildcareReason[]", List.of())
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isFalse();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isFalse();
        }

    }

    @Nested
    class whenParentHasPartnerWithReasonForChildcare {

        boolean hasPartner = true;

        @Test
        void skipsRecommendedDocumentsScreenIfNoneAreRequired() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .withParentPartnerDetails()
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of())
                    .with("activitiesParentPartnerChildcareReason[]", List.of())
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("controlId")).isTrue();
        }

        @Test
        void displaysJobsInstructionsWhenPartnerSelectsJob() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .withParentPartnerDetails()
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of())
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .with("activitiesParentPartnerChildcareReason[]", List.of("WORKING"))
                    .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234",
                            "false")
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displaySelfEmploymentInstructionsWhenPartnerSelectsSelfEmployment() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .withParentPartnerDetails()
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of())
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .with("activitiesParentPartnerChildcareReason[]", List.of("WORKING"))
                    .withPartnerJob("partnerJobs", "Company Name", "123 Main St", "Springfield", "IL", "60652", "(651) 123-1234",
                            "true")
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displaysTanfInstructionsWhenPartnerSelectsTanf() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .withParentPartnerDetails()
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of())
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .with("activitiesParentPartnerChildcareReason[]", List.of("TANF_TRAINING"))
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

        @Test
        void displaySchoolInstructionsWhenPartnerSelectsSchool() {
            testPage.navigateToFlowScreen("gcc/submit-ccap-terms");

            saveSubmission(getSessionSubmissionTestBuilder()
                    .withValidSubmissionUpTo7SignAndEmailWithSingleChildAndProvider(List.of(child_1), List.of(individualProvider))
                    .withParentPartnerDetails()
                    .with("parentHomeExperiencingHomelessness[]", List.of())
                    .with("activitiesParentChildcareReason[]", List.of())
                    .withMailingAddress("972 Mission St", "5", "San Francisco", "CA", "94103")
                    .with("activitiesParentPartnerChildcareReason[]", List.of("SCHOOL"))
                    .build()
            );

            navigatePassedSignedName(hasPartner);

            // doc-upload-recommended-docs
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-recommended-docs.title"));
            assertThat(testPage.elementDoesNotExistById("job-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-documentation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("education-documentation")).isFalse();
            assertThat(testPage.elementDoesNotExistById("tanf-training-recommendation")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homeless-documentation")).isTrue();
            testPage.clickButton(getEnMessage("doc-upload-recommended-docs.submit"));

            // doc-upload-add-files
            assertThat(testPage.getTitle()).isEqualTo(getEnMessage("doc-upload-add-files.title"));
            assertThat(testPage.elementDoesNotExistById("job-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("self-employment-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("school-upload-instruction")).isFalse();
            assertThat(testPage.elementDoesNotExistById("tanf-upload-instruction")).isTrue();
            assertThat(testPage.elementDoesNotExistById("homelessness-upload-instruction")).isTrue();
        }

    }

    private void navigatePassedSignedName(boolean hasQualifiedPartner) {
        // submit-ccap-terms
        testPage.clickElementById("agreesToLegalTerms-true");
        testPage.clickContinue();

        testPage.enter("signedName", "parent first parent last");
        if (hasQualifiedPartner) {
            testPage.enter("partnerSignedName", "partner parent");
        }
        testPage.clickButton(getEnMessage("submit-sign-name.submit-application"));

        // after-submit-contact-provider
        testPage.clickContinue();

        // contact-providers-start
        testPage.clickElementById("contactProviderMethod-OTHER-label");
        testPage.clickContinue();

        // contact-providers-share-code
        testPage.clickContinue();

        // contact-providers-review
        testPage.clickContinue();
    }
}
