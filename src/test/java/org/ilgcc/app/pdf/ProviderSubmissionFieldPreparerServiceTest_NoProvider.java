package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
public class ProviderSubmissionFieldPreparerServiceTest_NoProvider {

    @Autowired
    ProviderSubmissionFieldPreparerService preparer = new ProviderSubmissionFieldPreparerService();
    private Submission familySubmission;

    @BeforeEach
    void setUp() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .with("hasChosenProvider", "false")
                .build();
    }

    @Test
    public void whenEnableMultiProvidersIsOff() {
        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                new SingleField("providerResponseBusinessName", "No qualified provider", null));
        assertThat(result.get("providerResponseProviderNumber")).isEqualTo(
                new SingleField("providerResponseProviderNumber", "460328258720008", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "No provider chosen", null));
    }
}