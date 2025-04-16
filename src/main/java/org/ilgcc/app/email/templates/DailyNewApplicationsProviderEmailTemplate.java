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
import org.ilgcc.app.data.ResourceOrganizationTransaction;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.ILGCCEmailTemplate;
import org.ilgcc.app.utils.DateUtilities;
import org.springframework.context.MessageSource;

@Getter
@Setter
public class DailyNewApplicationsProviderEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale = Locale.ENGLISH;

    public DailyNewApplicationsProviderEmailTemplate(Map<String, Object> emailData, MessageSource messageSource) {
        this.emailData = emailData;
        this.messageSource = messageSource;
    }

    public ILGCCEmailTemplate createTemplate() {
        return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData.get("currentEmailDate").toString()),
                new Content("text/html", setBodyCopy(emailData)),
                EmailType.DAILY_NEW_APPLICATIONS_PROVIDER_EMAIL);
    }

    private Email senderEmail() {
        return new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(String date) {
        return messageSource.getMessage("email.automated-new-applications.subject", new Object[]{date},
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {

        List<ResourceOrganizationTransaction> transactions = (List) emailData.get("transactions");

        String p1 = messageSource.getMessage("email.automated-new-applications.header1",
                new Object[]{emailData.get("processingOrgName")},
                locale);

        String p2 = messageSource.getMessage("email.automated-new-applications.body1",
                new Object[]{emailData.get("transactionsAsOfDate")},
                locale);

        String p3 = messageSource.getMessage("email.automated-new-applications.header2", null,
                locale);

        String p4 = messageSource.getMessage("email.automated-new-applications.body2",
                new Object[]{transactions.size(), emailData.get("processingOrgName")},
                locale);

        String p5 = messageSource.getMessage("email.automated-new-applications.header3", null,
                locale);

        String p6 = "";

        if(!transactions.isEmpty()){
            p6 = String.format("<table style='border: 1px solid #b7b7b7; padding: 8px; border-collapse: collapse;'>%s</table>",
                    createTransactionsTableBody(transactions));
        }

        String p7 = messageSource.getMessage("email.automated-new-application.cta", null,
                locale);

        String p8 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p9 = messageSource.getMessage("email.general.footer.cfa", null, locale);

        return p1 + p2 + p3 + p4 + p5 + p6 + p7 + p8 + p9;
    }

    private String createTransactionsTableBody(List<ResourceOrganizationTransaction> transactions) {
        String tableHeader =
                "<thead style='font-weight: 800;background-color: #f3f3f3;'><tr><th style='width: 33.3%; border: 1px solid #b7b7b7; padding: 8px;'>"
                        + messageSource.getMessage("email.automated-new-applications.col-name-1", null,
                        locale) + "</th><th style='width: 33.3%; border: 1px solid #b7b7b7; padding: 8px;'>"
                        + messageSource.getMessage(
                        "email.automated-new-applications.col-name-2", null,
                        locale) + "</th><th style='width: 33.3%; border: 1px solid #b7b7b7'>" + messageSource.getMessage(
                        "email.automated-new-applications.col-name-3", null,
                        locale) + "</th></tr></thead>";

        List<String> tableBody = new ArrayList();
        transactions.forEach(r -> {
            tableBody.add(createSubmissionsTableRows(r));
        });

        return tableHeader + String.format("<tbody>%s</tbody>", String.join("", tableBody));
    }

    private String createSubmissionsTableRows(ResourceOrganizationTransaction transaction) {
        return "<tr>"
                + "<td style='border: 1px solid #b7b7b7; padding: 8px;'>" + DateUtilities.formatFullDateAndTimeinUTC(
                transaction.getCreatedAt()) + "</td>"
                + "<td style='border: 1px solid #b7b7b7; padding: 8px;'>" + transaction.getShortCode() + "</td>"
                + "<td style='border: 1px solid #b7b7b7; padding: 8px;'>" + transaction.getWorkItemId() + "</td>"
                + "</tr>";
    }

}
