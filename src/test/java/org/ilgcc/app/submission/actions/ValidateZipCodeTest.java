package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;

import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.Map;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.app.utils.ZipcodeOption;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
        classes = IlGCCApplication.class
)
@ActiveProfiles("test")
class ValidateZipCodeTest {

    @Autowired
    MessageSource messageSource;

    @Autowired
    ValidateZipCode action;

    @Test
    public void activeZipCodeReturnsTrue() {
        Submission submission = new SubmissionTestBuilder()
                .build();

        Map<String, Object> formData = Map.of(
                "applicationZipCode", ZipcodeOption.zip_60051.getValue()
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
}