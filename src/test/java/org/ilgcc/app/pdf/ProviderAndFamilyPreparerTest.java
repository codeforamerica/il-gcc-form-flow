package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderAndFamilyPreparerTest {

    @Autowired
    ProviderAndFamilyPreparer preparer;

    @Autowired
    protected SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void usesEarliestChildCareDateWhenProviderHasNotResponded() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .withDayCareProvider()
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("earliestChildcareStartDate", "11/12/2023")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "11/12/2023", null));
    }

    @Test
    public void usesEarliestChildCareDateWhenProviderStartDateIsNotSet() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .with("earliestChildcareStartDate", "01/12/2023")

                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "01/12/2023", null));

    }

    @Test
    public void usesProviderCareStartDateWhenPresent() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseAgreeToCare", "true")
                .with("providerCareStartDate", "12/11/2023")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .with("earliestChildcareStartDate", "11/11/2025")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "12/11/2023", null));
    }

    @Test
    public void ignoresProviderCareStartDateWhenNull() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .withProviderStateLicense()
                .with("providerResponseAgreeToCare", "true")
                .with("providerCareStartDate", "")
                .build();

        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withDayCareProvider()
                .withSubmittedAtDate(OffsetDateTime.now())
                .with("familyIntendedProviderName", "ProviderName")
                .with("familyIntendedProviderPhoneNumber", "(125) 785-67896")
                .with("familyIntendedProviderEmail", "mail@test.com")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .with("earliestChildcareStartDate", "11/11/2025")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("childcareStartDate")).isEqualTo(new SingleField("childcareStartDate", "11/11/2025", null));
    }

}
