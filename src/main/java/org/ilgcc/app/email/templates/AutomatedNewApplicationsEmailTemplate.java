package org.ilgcc.app.email.templates;

import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.ILGCCEmailTemplate;
import org.springframework.context.MessageSource;

@Getter
@Setter
public class AutomatedNewApplicationsEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public AutomatedNewApplicationsEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
        this.emailData = emailData;
        this.messageSource = messageSource;
        this.locale = locale;

    }

    public ILGCCEmailTemplate createTemplate() {
        return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData.getOrDefault("date", "").toString()),
                new Content("text/html", setBodyCopy(emailData)),
                EmailType.AUTOMATED_NEW_APPLICATIONS_EMAIL);
    }

    private Email senderEmail() {
        return new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(String date) {
        return messageSource.getMessage("email.automated-new-applications.subject", new Object[]{date},
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        List<Map<String, String>> submissions = (List) emailData.get("submissions");

        String p1 = messageSource.getMessage("email.automated-new-applications.header1",
                new Object[]{emailData.get("processingOrg")},
                locale);

        String p2 = messageSource.getMessage("email.automated-new-applications.body1",
                new Object[]{emailData.get("currentEmailDate")},
                locale);

        String p3 = messageSource.getMessage("email.automated-new-applications.header2", null,
                locale);

        String p4 = messageSource.getMessage("email.automated-new-applications.body2",
                new Object[]{submissions.size(), emailData.get("processingOrg")},
                locale);

        String p5 = messageSource.getMessage("email.automated-new-applications.header3", null,
                locale);

        String p6 = String.format("<table class='submissions-list'>%s</table>", createSubmissionsTableBody(submissions));

        return p1 + p2 + p3 + p4 + p5 + p6;
    }

    private String createSubmissionsTableBody(List<Map<String, String>> submissions) {
        String tableHeader =
                "<thead><tr><th scope = 'col'>" + messageSource.getMessage("email.automated-new-applications.col-name-1", null,
                        locale) + "</th>" + "<th scope = 'col'>" + messageSource.getMessage(
                        "email.automated-new-applications.col-name-2", null,
                        locale) + "</th>" + "<th scope = 'col'>" + messageSource.getMessage(
                        "email.automated-new-applications.col-name-3", null,
                        locale) + "</th></tr></thead>";

        List<String> tableBody = new ArrayList();
        submissions.forEach(s -> {
            tableBody.add(createSubmissionsTableRows(s));
        });

        return tableHeader + String.format("<tbody>%s/<tbody>", String.join("", tableBody));
    }

    private String createSubmissionsTableRows(Map<String, String> submission) {
        return "<tr>"
                + "<td>" + submission.get("date") + "</td>"
                + "<td>" + submission.get("confirmationCode") + "</td>"
                + "<td>" + submission.get("workItemID") + "</td>"
                + "</tr>";
    }

}
