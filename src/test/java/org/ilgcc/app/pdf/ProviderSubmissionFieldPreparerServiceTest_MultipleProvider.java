package org.ilgcc.app.pdf;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import formflow.library.pdf.SingleField;
import formflow.library.pdf.SubmissionField;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.enums.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.enable-multiple-providers=true"

)
@ActiveProfiles("test")
public class ProviderSubmissionFieldPreparerServiceTest_MultipleProvider {

    @Autowired
    private ProviderSubmissionFieldPreparerService preparer;

    @Autowired
    private SubmissionRepositoryService submissionRepositoryService;

    private Submission familySubmission;

    private static String FAM_INTENDED_PROVIDER_NAME = "IntendedProviderName";
    private static String FAM_INTENDED_PROVIDER_PHONE = "(125) 785-67896";
    private static String FAM_INTENDED_PROVIDER_EMAIL = "mail@test.com";

    @BeforeEach
    void setUp() {
        Map<String, Object> provider = new HashMap<>();
        provider.put("uuid", "first-provider-uuid");
        provider.put("iterationIsComplete", true);
        provider.put("familyIntendedProviderName", FAM_INTENDED_PROVIDER_NAME);
        provider.put("familyIntendedProviderEmail", FAM_INTENDED_PROVIDER_EMAIL);
        provider.put("familyIntendedProviderPhoneNumber", FAM_INTENDED_PROVIDER_PHONE);

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .with("providers[]", List.of(provider))
                .build();

    }

    @Nested
    class whenProviderHasNotResponded {

        @Test
        public void whenApplicationIsActive() {
            familySubmission.getInputData().put("providerSubmissionStatus", SubmissionStatus.ACTIVE.name());

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);
            assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                    new SingleField("providerResponseBusinessName", FAM_INTENDED_PROVIDER_NAME, null));
            assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                    new SingleField("providerResponseContactPhoneNumber", FAM_INTENDED_PROVIDER_PHONE, null));
            assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                    new SingleField("providerResponseContactEmail", FAM_INTENDED_PROVIDER_EMAIL, null));
            assertThat(result.get("providerResponse")).isNull();
        }
    }

}
