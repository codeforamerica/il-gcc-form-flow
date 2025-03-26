package org.ilgcc.app.submission.actions;

import static org.assertj.core.api.Assertions.assertThat;


import formflow.library.data.Submission;
import java.util.List;
import org.ilgcc.app.IlGCCApplication;
import org.ilgcc.app.submission.conditions.DisplaySubmitContactProviderEmailScreen;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
@SpringBootTest(
        classes = IlGCCApplication.class,
        properties = "il-gcc.enable-provider-messaging=true"
)

@ActiveProfiles("test")
public class DisplaySubmitContactProviderEmailScreenTest {

    @Autowired
    DisplaySubmitContactProviderEmailScreen action;

    private String emailInputName = "familyIntendedProviderEmail";

    private String contactMethodInputName = "contactProviderMethod[]";

    @Test
    public void skipsScreenIfContactProviderMethodIsNotEmail() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with(contactMethodInputName, List.of("OTHER"))
                .with(emailInputName, "")
                .build();

        assertThat(action.run(submission)).isEqualTo(false);
    }

    @Test
    public void showsScreenIfContactProviderMethodIsEmailAndEmailInputIsBlank() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with(contactMethodInputName, List.of("EMAIL"))
                .with(emailInputName, "")
                .build();

        assertThat(action.run(submission)).isEqualTo(true);
    }

    @Test
    public void showsScreenIfContactProviderMethodIsEmailAndEmailInputIsMissing() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with(contactMethodInputName, List.of("EMAIL"))
                .build();

        assertThat(action.run(submission)).isEqualTo(true);
    }

    @Test
    public void skipsScreenIfContactProviderMethodIsEmailAndEmailInputIsNotBlank() {
        Submission submission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with(contactMethodInputName, List.of("EMAIL"))
                .with(emailInputName, "mail@mail.com")
                .build();

        assertThat(action.run(submission)).isEqualTo(false);
    }
}
