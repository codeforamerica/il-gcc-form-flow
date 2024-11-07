package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import jakarta.servlet.http.HttpSession;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GenerateDummyClientSubmissionForDev implements Action {
    
    private final SubmissionRepositoryService submissionRepositoryService;
    private final SubmissionRepository submissionRepository;
    private final HttpSession httpSession;
    
    @Autowired
    Environment env;
    
    public GenerateDummyClientSubmissionForDev(SubmissionRepositoryService submissionRepositoryService,
            SubmissionRepository submissionRepository, HttpSession httpSession) {
        this.submissionRepositoryService = submissionRepositoryService;
        this.submissionRepository = submissionRepository;
        this.httpSession = httpSession;
    }

    @Override
    public void run(Submission submission) {
        String[] activeProfiles = env.getActiveProfiles();
        boolean isDevProfile = Arrays.asList(activeProfiles).contains("dev");
        if (isDevProfile) {
            Optional<Submission> existingDummyClientSubmision = submissionRepositoryService.findByShortCode("DEV-123ABC");
            existingDummyClientSubmision.ifPresent(submissionRepository::delete);

            Map<String, Object> inputData = new HashMap<>();
            inputData.put("familyIntendedProviderName", "Dev Provider");
            inputData.put("parentFirstName", "Devy");
            inputData.put("parentLastName", "McDeverson");
            Submission dummyClientSubmission = new Submission();
            dummyClientSubmission.setSubmittedAt(OffsetDateTime.now().minusDays(1));
            dummyClientSubmission.setFlow("gcc");
            dummyClientSubmission.setShortCode("DEV-123ABC");
            dummyClientSubmission.setInputData(inputData);
            
            submissionRepositoryService.save(dummyClientSubmission);

            httpSession.setAttribute("clientSubmissionId", dummyClientSubmission.getId());
        }
    }
}
