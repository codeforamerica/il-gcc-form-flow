package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@TestPropertySource(properties = {
        "ACTIVE_CASELOAD_CODES=BB,QQ",
})
@ActiveProfiles("test")
class ValidateZipCodeTest {

    private static final String INVALID_CHAMPAIGN_ZIPCODE = "60949";
    private static final String VALID_MCHENRY_ZIPCODE = "60002";
    private static final String VALID_SDA15_ZIPCODE = "60015";
    private static final String INVALID_ZIPCODE_LENGTH = VALID_MCHENRY_ZIPCODE + "2212334344";
    @Autowired
    ValidateZipCode action;

    @Test
    public void inactiveZipCodeReturnsFalse() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        Map<String, Object> formData = Map.of(
                "applicationZipCode", INVALID_CHAMPAIGN_ZIPCODE
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.runValidation(formSubmission, submission);

        assertThat(submission.getInputData().get("hasValidZipCode")).isEqualTo("false");
    }

    @Test
    public void activeZipCodeReturnsTrue() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        Map<String, Object> formData = Map.of(
                "applicationZipCode", VALID_MCHENRY_ZIPCODE
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.runValidation(formSubmission, submission);

        assertThat(submission.getInputData().get("hasValidZipCode")).isEqualTo("true");
    }

    @Test
    public void outofStateZipCodeReturnsFalse() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        Map<String, Object> formData = Map.of(
                "applicationZipCode", "94114"
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.runValidation(formSubmission, submission);

        assertThat(submission.getInputData().get("hasValidZipCode")).isEqualTo("false");
    }

    @Test
    public void sda15ZipCodeReturnsTrue() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        Map<String, Object> formData = Map.of(
                "applicationZipCode", VALID_SDA15_ZIPCODE
        );

        FormSubmission formSubmission = new FormSubmission(formData);


        action.runValidation(formSubmission, submission);

        assertThat(submission.getInputData().get("hasValidZipCode")).isEqualTo("true");
    }

    @Test
    public void returnsHasValidZipCodeIsFalseWhenZipCodeIsMoreThan5Digits() {
        Submission submission = new SubmissionTestBuilder()
            .build();

        Map<String, Object> formData = Map.of(
            "applicationZipCode", INVALID_ZIPCODE_LENGTH
        );

        FormSubmission formSubmission = new FormSubmission(formData);

        action.runValidation(formSubmission, submission);
        assertThat(submission.getInputData().get("hasValidZipCode")).isEqualTo("false");
    }
}