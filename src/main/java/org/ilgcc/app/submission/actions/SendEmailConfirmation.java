package org.ilgcc.app.submission.actions;


import com.mailgun.model.message.MessageResponse;
import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.email.MailgunEmailClient;
import formflow.library.pdf.PdfService;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendEmailConfirmation implements Action {

  private final MessageSource messageSource;
  private final PdfService pdfService;
  private final MailgunEmailClient mailgunEmailClient;

  @Value("${form-flow.flow.ubi.email.confirmation.cc:}")
  private List<String> emailToCc;

  @Value("${form-flow.flow.ubi.email.confirmation.bcc:}")
  private List<String> emailToBcc;

  public SendEmailConfirmation(MessageSource messageSource, PdfService pdfService, MailgunEmailClient mailgunEmailClient) {
    this.messageSource = messageSource;
    this.pdfService = pdfService;
    this.mailgunEmailClient = mailgunEmailClient;
  }

  @Override
  public void run(Submission submission) {
    List<String> howToContactYou = (List<String>) submission.getInputData().get("howToContactYou[]");
    boolean agreesToEmailContact = howToContactYou.stream().anyMatch(contactType -> contactType.equals("email"));
    if (!agreesToEmailContact) {
      return;
    }
    String recipientEmail = (String) submission.getInputData().get("email");
    if (recipientEmail == null || recipientEmail.isBlank()) {
      return;
    }
//    Optional<Boolean> requireTls = Optional.empty();

//    String emailSubject = messageSource.getMessage("email.subject", null, null);
//    Object[] args = new Object[]{submission.getId().toString()};
//    String emailBody = messageSource.getMessage("email.body", args, null);
    List<File> pdfs = new ArrayList<File>();
//    generateApplicationAndAttachToEmail(recipientEmail, emailSubject, emailToCc, emailToBcc, emailBody, pdfs, requireTls, submission);

    String nextStepsSubject = messageSource.getMessage("next-steps-email.subject", null, null);
    String nextStepsBody = messageSource.getMessage("next-steps-email.body", null, null);
    pdfs = new ArrayList<>();
    MessageResponse nextStepsResponse = mailgunEmailClient.sendEmail(
        nextStepsSubject,
        recipientEmail,
        Collections.emptyList(),
        Collections.emptyList(),
        nextStepsBody,
        pdfs
    );
    boolean confirmationWasQueued = nextStepsResponse.getMessage().contains("Queued. Thank you.");
    log.info("Mailgun MessageResponse confirms message was queued (true/false): " + confirmationWasQueued);
    submission.getInputData().put("confirmationEmailQueued", Boolean.toString(confirmationWasQueued));
    submission.setInputData(submission.getInputData());
  }
}
