package org.ilgcc.app.submission.actions;

import formflow.library.config.submission.Action;
import formflow.library.data.Submission;
import formflow.library.data.SubmissionRepository;
import formflow.library.data.SubmissionRepositoryService;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class RemoveDummySubmissionForDev implements Action {
    SubmissionRepository submissionRepository;
    SubmissionRepositoryService submissionRepositoryService;
    
    @Autowired
    Environment env;
    
    public RemoveDummySubmissionForDev(SubmissionRepository submissionRepository, 
            SubmissionRepositoryService submissionRepositoryService) {
        this.submissionRepository = submissionRepository;
        this.submissionRepositoryService = submissionRepositoryService;
    }
    
    @Override
    public void run(Submission submission) {
        String[] activeProfiles = env.getActiveProfiles();
        boolean isDevProfile = Arrays.asList(activeProfiles).contains("dev");
        if (isDevProfile) {
            submissionRepositoryService.findByShortCode("DEV-123ABC").ifPresent(submissionRepository::delete);
        }
    }
}
