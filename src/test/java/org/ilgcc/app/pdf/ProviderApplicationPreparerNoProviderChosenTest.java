package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;
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
public class ProviderApplicationPreparerNoProviderChosenTest {

    @Autowired
    private ProviderApplicationPreparer preparer;

    private Submission familySubmission;

    @Test
    public void mapsFieldsCorrectlyWhenNoProviderWasChosen() {
        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("hasChosenProvider", "false")
                .build();

        Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
        assertThat(result.get("providerNameCorporate")).isEqualTo(new SingleField("providerNameCorporate", "No qualified provider", null));
        assertThat(result.get("dayCareIdNumber")).isEqualTo(new SingleField("dayCareIdNumber", "460328258720008", null));
        assertThat(result.get("providerResponse")).isEqualTo(
                new SingleField("providerResponse", "No provider chosen", null));
    }
}
