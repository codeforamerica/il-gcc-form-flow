package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.ilgcc.app.utils.ProviderSubmissionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FormatChildrenWhoNeedCareNames implements Action {
    
    @Autowired
    SubmissionRepositoryService submissionRepositoryService;

    @Autowired
    MessageSource messageSource;

    @Override
    public void run(Submission providerSubmission) {
        Optional<UUID> familySubmissionId = ProviderSubmissionUtilities.getFamilySubmissionId(providerSubmission);
        if (familySubmissionId.isPresent()) {
            Submission familySubmission = submissionRepositoryService.findById(familySubmissionId.get()).get();
            Locale locale = LocaleContextHolder.getLocale();
            String joiner = messageSource.getMessage("general.and", null, locale);

            String formattedChildrenNames = ProviderSubmissionUtilities.formatChildNamesAsCommaSeparatedList(familySubmission, joiner);
            providerSubmission.getInputData().put("childrenWhoNeedCareNames", formattedChildrenNames);
        }
    }
}
