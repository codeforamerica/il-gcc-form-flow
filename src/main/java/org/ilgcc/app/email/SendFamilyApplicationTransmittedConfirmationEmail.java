package org.ilgcc.app.email;

import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getFamilySubmissionDataForEmails;
import static org.ilgcc.app.utils.ProviderSubmissionUtilities.getProviderSubmissionDataForEmails;

import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.email.templates.FamilyApplicationTransmittedConfirmationEmailTemplate;
import org.ilgcc.app.utils.DateUtilities;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.ilgcc.app.utils.SchedulePreparerUtility;
import org.ilgcc.jobs.SendEmailJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SendFamilyApplicationTransmittedConfirmationEmail extends SendEmail {

    @Autowired
    public SendFamilyApplicationTransmittedConfirmationEmail(SendEmailJob sendEmailJob, MessageSource messageSource,
            SubmissionRepositoryService submissionRepositoryService) {
        super(sendEmailJob, messageSource, submissionRepositoryService, "familyApplicationTransmittedEmailSent",
                "parentContactEmail");
    }

    @Override
    protected ILGCCEmailTemplate emailTemplate(Map<String, Object> emailData) {
        return new FamilyApplicationTransmittedConfirmationEmailTemplate(emailData, messageSource, locale).createTemplate();
    }

    @Override
    protected Optional<Map<String, Object>> getEmailData(Submission familySubmission, Map<String, Object> subflowData) {
        Map<String, Object> emailData = new HashMap<>();
        emailData.putAll(getFamilySubmissionDataForEmails(familySubmission, subflowData));

        List<Map<String, Object>> providers = (List<Map<String, Object>>) familySubmission.getInputData()
                .getOrDefault("providers", Collections.EMPTY_LIST);

        List<Map<String, Object>> providerData = new ArrayList<>();

        Map<String, List<Map<String, Object>>> providerSchedules = (Map<String, List<Map<String, Object>>>) SchedulePreparerUtility.getRelatedChildrenSchedulesForProvider(
                familySubmission.getInputData());

        if (!providerSchedules.isEmpty()) {
            for (String providerId : providerSchedules.keySet()) {
                if (!"NO_PROVIDER".equals(providerId)) {
                    Map<String, Object> currentProviderData = new HashMap<>();
                    Optional<Map<String, Object>> currentProvider = providers.stream()
                            .filter(provider -> provider.get("uuid").equals(providerId)).findFirst();
                    if (currentProvider.isPresent()) {
                        String earliestCCAPDateForCurrentProvider =
                                (String) DateUtilities.getEarliestDate(providerSchedules.get(providerId).stream().map(s -> s.get("ccapStartDate").toString()).toList());

                        if (currentProvider.get().containsKey("providerResponseSubmissionId")) {
                            Optional<Submission> currentProviderSubmission = submissionRepositoryService.findById(
                                    UUID.fromString(currentProvider.get().get("providerResponseSubmissionId").toString()));
                            currentProviderData.put("ccapStartDate",
                                    ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(
                                            earliestCCAPDateForCurrentProvider,
                                            currentProviderSubmission));
                            if (currentProviderSubmission.isPresent()) {
                                currentProviderData.putAll(getProviderSubmissionDataForEmails(currentProviderSubmission.get()));

                            }
                        } else {
                            currentProviderData.put("ccapStartDate",
                                    ProviderSubmissionUtilities.getCCAPStartDateFromProviderOrFamilyChildcareStartDate(
                                            earliestCCAPDateForCurrentProvider,
                                            Optional.empty()));
                        }

                        currentProviderData.putAll(currentProvider.get());
                        currentProviderData.put("childrenInitialsList",
                                ProviderSubmissionUtilities.getChildrenInitialsList(providerSchedules.get(providerId)));
                        providerData.add(currentProviderData);
                    }

                }

                emailData.put("providersData", providerData);
            }
        }

        return Optional.of(emailData);
    }

}

