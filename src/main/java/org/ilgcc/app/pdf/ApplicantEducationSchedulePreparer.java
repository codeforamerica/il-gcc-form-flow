package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import org.springframework.stereotype.Component;

@Component
public class ApplicantEducationSchedulePreparer extends SchedulePreparer {

    @Override
    public void setSchedule(Submission submission) {
        schedule = getHourlySchedule(submission, "activitiesClass", "weeklySchedule[]");
        fieldPrefixKey = "applicantEducationSchedule";
    }

}