package org.ilgcc.app.submission.actions;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.time.OffsetDateTime;
import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.ilgcc.app.utils.SubmissionTestBuilder;
import org.ilgcc.jobs.SendEmailJob;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;


@ActiveProfiles("test")
@SpringBootTest
public class SendProviderConfirmationEmailTest {

    @Autowired
    private SendProviderConfirmationEmail action;

    private Submission providerSubmission;

    private Submission  familySubmission;

    @MockitoSpyBean
    SendEmailJob sendEmailJob;

    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @BeforeEach
    void setUp() {
         familySubmission = new SubmissionTestBuilder()
                .withFlow("gcc")
                .with("parentPreferredName", "FirstName").withChild("First", "Child", "Yes").withChild("Second", "Child", "Yes")
                .withSubmittedAtDate(OffsetDateTime.now())
                .withShortCode("ABC123")
                .build();

        submissionRepositoryService.save(familySubmission);
    }

    void whenCompleteProviderInformation(){
        providerSubmission = new SubmissionTestBuilder()
                .withFlow("providerresponse")
                .with("familySubmissionId", familySubmission.getId().toString())
                .with("providerResponseContactEmail", "provideremail@test.com")
                .with("providerCareStartDay", "01/10/2025")
                .build();

        submissionRepositoryService.save(providerSubmission);

        action.run(providerSubmission);

//        assert that the providerSubmission providerConfirmationEmailSent is set to true
        // assert that sendEmailJob.enqueueSendEmailJob is called with the expected parameters

        // or call each method and and confirm that the correct email copy etc is called
        // assert that SendEmailJob.enqueuSendEmailJob was called once


    }

}