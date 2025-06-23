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

    private Submission familySubmission;

    private static String FAM_INTENDED_PROVIDER_NAME = "ChildCareProgramName";
    private static String FAM_INTENDED_PROVIDER_PHONE = "(125) 785-67896";
    private static String FAM_INTENDED_PROVIDER_EMAIL = "mail@test.com";

    @BeforeEach
    void setUp() {
        Map<String, Object> provider = new HashMap<>();
        provider.put("uuid", "first-provider-uuid");
        provider.put("iterationIsComplete", true);
        provider.put("childCareProgramName", FAM_INTENDED_PROVIDER_NAME);
        provider.put("familyIntendedProviderEmail", FAM_INTENDED_PROVIDER_EMAIL);
        provider.put("familyIntendedProviderPhoneNumber", FAM_INTENDED_PROVIDER_PHONE);

        Map<String, Object> child = new HashMap<>();
        child.put("uuid", "child-uuid");
        child.put("childFirstName", "First");
        child.put("childLastName", "Child");
        child.put("childInCare", "true");
        child.put("childDateOfBirthMonth", "10");
        child.put("childDateOfBirthDay", "11");
        child.put("childDateOfBirthYear", "2002");
        child.put("needFinancialAssistanceForChild", true);
        child.put("childIsUsCitizen", "Yes");
        child.put("ccapStartDate", "01/10/2025");

        familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .withSubmittedAtDate(OffsetDateTime.now().minusDays(10))
                .with("providers", List.of(provider))
                .with("children", List.of(child))
                .withChildcareScheduleForProvider("child-uuid", "first-provider-uuid")
                .build();
    }

    @Nested
    class whenProviderHasNotResponded {

        @Test
        public void whenApplicationIsActive() {
            familySubmission.getInputData().put("providerSubmissionStatus", SubmissionStatus.ACTIVE.name());

            Map<String, SubmissionField> result = preparer.prepareSubmissionFields(familySubmission, null);

            assertThat(result.get("childFirstName_1")).isEqualTo(new SingleField("childFirstName", "First", 1));
            assertThat(result.get("childLastName_1")).isEqualTo(new SingleField("childLastName", "Child", 1));

            assertThat(result.get("providerResponseBusinessName")).isEqualTo(
                    new SingleField("providerResponseBusinessName", FAM_INTENDED_PROVIDER_NAME, null));
            assertThat(result.get("providerResponseContactPhoneNumber")).isEqualTo(
                    new SingleField("providerResponseContactPhoneNumber", FAM_INTENDED_PROVIDER_PHONE, null));
            assertThat(result.get("providerResponseContactEmail")).isEqualTo(
                    new SingleField("providerResponseContactEmail", FAM_INTENDED_PROVIDER_EMAIL, null));
            assertThat(result.get("providerResponse")).isNull();

            assertThat(result.get("childCareScheduleMondayStart_1")).isEqualTo(
                    new SingleField("childCareScheduleMondayStart", "10:24", 1));
            assertThat(result.get("childCareScheduleMondayStartAmPm_1")).isEqualTo(
                    new SingleField("childCareScheduleMondayStartAmPm", "AM", 1));

            assertThat(result.get("childCareScheduleTuesdayStart_1")).isEqualTo(
                    new SingleField("childCareScheduleTuesdayStart", "10:24", 1));
            assertThat(result.get("childCareScheduleTuesdayStartAmPm_1")).isEqualTo(
                    new SingleField("childCareScheduleTuesdayStartAmPm", "AM", 1));

            assertThat(result.get("childCareScheduleMondayEnd_1")).isEqualTo(
                    new SingleField("childCareScheduleMondayEnd", "10:21", 1));
            assertThat(result.get("childCareScheduleMondayEndAmPm_1")).isEqualTo(
                    new SingleField("childCareScheduleMondayEndAmPm", "PM", 1));
            assertThat(result.get("childCareScheduleTuesdayEnd_1")).isEqualTo(
                    new SingleField("childCareScheduleTuesdayEnd", "10:21", 1));
            assertThat(result.get("childCareScheduleTuesdayEndAmPm_1")).isEqualTo(
                    new SingleField("childCareScheduleTuesdayEndAmPm", "PM", 1));
        }
    }

}
