package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
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
public class ProviderSSNPreparerTest {


    @Autowired
    private ProviderSSNPreparer preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;
    private Submission providerSubmission;

    @Test
    public void shouldPrintProviderIdentityCheckSSNToPDFIfPresent() {
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .withProviderSubmissionData()
                .with("providerIdentityCheckSSN", "445-32-6666")
                .with("providerTaxIdSSN", "555-55-5555")
                .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("providerResponseSubmissionId", providerSubmission.getId())
                .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "445-32-6666", null));
    }
    @Test
    public void shouldPrintProviderTaxIdSSNIfPresent() {
        providerSubmission = new SubmissionTestBuilder()
            .withFlow("providerresponse")
            .withProviderSubmissionData()
            .with("providerTaxIdSSN", "444-44-4444")
            .build();
        submissionRepositoryService.save(providerSubmission);

        familySubmission = new SubmissionTestBuilder()
            .withFlow("gcc")
            .with("providerResponseSubmissionId", providerSubmission.getId())
            .build();
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerSSN")).isEqualTo(new SingleField("providerSSN", "444-44-4444", null));
    }
}
