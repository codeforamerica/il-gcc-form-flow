package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.FormSubmission;
import formflow.library.data.Submission;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.SendProviderAgreesToCareFamilyConfirmationEmail;
import org.ilgcc.app.email.SendProviderConfirmationEmail;
import org.ilgcc.app.email.SendProviderDeclinesCareFamilyConfirmationEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PopulateContactProvidersIteration implements Action {

    @Override
    public void run(Submission submission, String subflowUuid) {
        Map<String, Object> contactProvidersSubflow = submission.getSubflowEntryByUuid("contactProviders", subflowUuid);
        if (contactProvidersSubflow.containsKey("providerUuid")) {
            String providerUuid = (String) contactProvidersSubflow.get("providerUuid");
            Map<String, Object> providersSubflow = submission.getSubflowEntryByUuid("providers", providerUuid);
            if (providersSubflow != null) {

                Map<String, Object> contactData = contactProvidersSubflow;
                contactData.put("familyIntendedProviderName", providersSubflow.get("familyIntendedProviderName"));
                contactData.put("familyIntendedProviderEmail", providersSubflow.get("familyIntendedProviderEmail"));
                contactData.put("familyIntendedProviderPhoneNumber", providersSubflow.get("familyIntendedProviderPhoneNumber"));

                submission.mergeFormDataWithSubflowIterationData("contactProviders", contactProvidersSubflow, contactData);
            }
        }
    }
}
