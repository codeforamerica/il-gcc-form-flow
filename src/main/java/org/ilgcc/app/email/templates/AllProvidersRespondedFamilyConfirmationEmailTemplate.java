package org.ilgcc.app.email.templates;

import static java.util.Collections.emptyList;
import static org.ilgcc.app.email.ILGCCEmail.FROM_ADDRESS;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;
import org.ilgcc.app.email.ILGCCEmail;
import org.ilgcc.app.email.ILGCCEmail.EmailType;
import org.ilgcc.app.email.ILGCCEmailTemplate;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.context.MessageSource;

@Getter
@Setter
public class AllProvidersRespondedFamilyConfirmationEmailTemplate {

    private Map<String, Object> emailData;
    private MessageSource messageSource;
    private Locale locale;

    public AllProvidersRespondedFamilyConfirmationEmailTemplate(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
       this.emailData = emailData;
       this.messageSource = messageSource;
       this.locale = locale;

   }
   public ILGCCEmailTemplate createTemplate(){
       return new ILGCCEmailTemplate(senderEmail(), setSubject(emailData), new Content("text/html", setBodyCopy(emailData)), EmailType.ALL_PROVIDERS_RESPONDED_FAMILY_CONFIRMATION_EMAIL);
   }

    private Email senderEmail() {
        return  new Email(FROM_ADDRESS, messageSource.getMessage(ILGCCEmail.EMAIL_SENDER_KEY, null, locale));
    }

    private String setSubject(Map<String, Object> emailData) {
        return messageSource.getMessage("email.all-providers-responded-family-confirmation-email.subject",null,
                locale);
    }

    private String setBodyCopy(Map<String, Object> emailData) {
        String p1 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p1", new Object[]{emailData.get("parentFirstName")},
                locale);
        String p2 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p2", new Object[]{emailData.get("ccrrName")}, locale);
        String allProviderResponses = getAllProviderResponses(emailData, messageSource, locale);
        String p3 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p3",
                new Object[]{emailData.get("confirmationCode")},
                locale);
        String p4 = messageSource.getMessage("email.all-providers-responded-family-confirmation-email.p4", new Object[]{emailData.get("ccrrName"), emailData.get("ccrrPhoneNumber")}, locale);
        String p5 = messageSource.getMessage("email.general.footer.automated-response", null, locale);
        String p6 = messageSource.getMessage("email.general.footer.cfa", null, locale);
        return p1 + p2 + allProviderResponses + p3 + p4 + p5 + p6;
    }
  private String getAllProviderResponses(Map<String, Object> emailData, MessageSource messageSource, Locale locale) {
      List<Map<String, Object>> ccapChildrenSchedules = (List<Map<String, Object>>) Optional.ofNullable(emailData.get("childcareSchedules")).orElse(
          emptyList());
      String providerResponses = "";
      for (Map<String, Object> childSchedule : ccapChildrenSchedules) {

        //get child information associated with the childcare schedule by childUuid
        List<Map<String, Object>> children = (List<Map<String, Object>>) emailData.get("children");
        Map<String, Object> child = children.stream().filter(currentChild -> currentChild.get("uuid").equals(childSchedule.get("childUuid"))).findFirst().orElse(new HashMap<>());
        String childInitials = ProviderSubmissionUtilities.getInitials((String) child.get("childFirstName"), (String) child.get("childLastName"));
        //iterate over the provider schedules found
        List<Map<String, Object>> providerSchedules = (List<Map<String, Object>>) childSchedule.getOrDefault("providerSchedules", emptyList());
        if (!providerSchedules.isEmpty()) {
          for (Map<String, Object> providerSchedule : providerSchedules) {
            //get provider from providerSchedule
            List<Map<String, Object>> providers = (List<Map<String, Object>>) emailData.get("providers");
            Map<String, Object> provider = providers.stream().filter(currentProvider -> currentProvider.get("uuid").equals(providerSchedule.get("repeatForValue"))).findFirst().orElse(new HashMap<>());

            //create list elements based on whether provider agrees to care.  The fallback else statement will be that the provider did not respond in three days because of where this code will be located.
            if (provider.getOrDefault("providerResponseAgreeToCare", "").equals("true")){
              providerResponses = String.format("%s%s", providerResponses,
                  messageSource.getMessage("email.all-providers-responded-family-confirmation-email.li-agreed-to-care", new Object[]{provider.get("providerResponseName"), childInitials ,providerSchedule.get("ccapStartDate")}, locale));
            } else if (provider.getOrDefault("providerResponseAgreeToCare", "").equals("false")){
              providerResponses = String.format("%s%s", providerResponses,
                  messageSource.getMessage("email.all-providers-responded-family-confirmation-email.li-did-not-agree-to-care", new Object[]{provider.get("providerResponseName"), childInitials}, locale));
            } else{
              providerResponses = String.format("%s%s", providerResponses,
                  messageSource.getMessage("email.all-providers-responded-family-confirmation-email.li-did-not-complete-application-in-three-days", new Object[]{provider.get("providerResponseName")}, locale));
            }
          }
        }
      }
      return String.format("<ul>%s</ul>", providerResponses);
  }
}
