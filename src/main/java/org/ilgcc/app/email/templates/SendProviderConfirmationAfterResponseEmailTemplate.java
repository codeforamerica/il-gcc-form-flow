package org.ilgcc.app.email.templates;

import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
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
public class SendProviderConfirmationAfterResponseEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

   public SendProviderConfirmationAfterResponseEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
       this.emailData = emailData;
       this.messageSource = messageSource;
       this.locale = locale;

   }
   public ILGCCEmailTemplate createTemplate(){
       return new ILGCCEmailTemplate(senderEmail(), setSubject(), new Content("text/html", setBodyCopy(emailData)), EmailType.PROVIDER_CONFIRMATION_AFTER_RESPONSE_EMAIL);
   }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject() {
        return messageSource.getMessage("email.general.subject.confirmation-code", new Object[]{emailData.get("confirmationCode")}, locale);
    }
    //TODO: SET UP EMAIL DATA TO GET CHILDREN INITIALS, CCAP START DATE, CCRRNAME, CONFIRMATION_CODE
    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.provider-confirmation-after-response.p1", null, locale);
        String p2 = messageSource.getMessage("email.provider-confirmation-after-response.p2",null, locale);
        String p3 = messageSource.getMessage("email.provider-confirmation-after-response.p3",
                new Object[]{emailData.get("childrenInitials"), emailData.get("ccapStartDate")}, locale);
        String p4 = messageSource.getMessage("email.provider-confirmation-after-response.p4", new Object[]{emailData.get("confirmationCode")},
                locale);
        String p5 = messageSource.getMessage("email.provider-confirmation-after-response.p5", new Object[]{emailData.get("ccrrName")}, locale);
        String familyHasMoreThanOneProviderParagraph = messageSource.getMessage("email.provider-confirmation-after-response.p6", null, locale);
        String familyIsNewProviderParagraph = messageSource.getMessage("email.provider-confirmation-after-response.p7", new Object[]{emailData.get("ccrrName")}, locale);
        String familyIsNewProviderList = messageSource.getMessage("email.provider-confirmation-after-response.p8.ul", null, locale);

        String p6 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p7 = messageSource.getMessage("email.general.footer.cfa", null, locale);


        StringBuilder finalEmail = new StringBuilder();
        finalEmail.append(p1)
            .append(p2).append(p3).append(p4).append(p5);
        String hasMultipleProvidersWithChildcareSchedules = emailData.getOrDefault("hasMultipleProvidersWithChildcareSchedules", "false").toString();
        String hasMultipleProviders = emailData.getOrDefault("hasMultipleProviders", "false").toString();
        String isReturningProvider = emailData.getOrDefault("providerHasBeenPaidByCCAP", "false").toString();

        //if there are multiple providers with provider schedules then we need to the text for more than one provider
        if(hasMultipleProviders.equals("true") && hasMultipleProvidersWithChildcareSchedules.equals("true")) {
          finalEmail.append(familyHasMoreThanOneProviderParagraph);
        }

//      if the provider is a new provider then we need to display the prompt for a new provider
        if (isReturningProvider.equals("false")) {
          finalEmail.append(familyIsNewProviderParagraph);
          finalEmail.append(familyIsNewProviderList);
        }

        //We need to add the footer to the message once we are done
        finalEmail.append(p6).append(p7);
        return finalEmail.toString();
    }

}
