package org.ilgcc.app.pdf;

import formflow.library.data.Submission;
import formflow.library.pdf.PdfMap;
import formflow.library.pdf.SubmissionField;
import org.ilgcc.app.utils.ActivitySchedules.HourlySchedule;
import org.ilgcc.app.utils.ActivitySchedules.LocalTimeRange;
import org.ilgcc.app.utils.DayOfWeekOption;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.*;


@Component
public class ApplicantEducationSchedulePreparer extends SchedulePreparer {

    @Override
    public void setActivitiesClassSchedule(Submission submission) {
        activitiesClassSchedule = getHourlySchedule(submission, "activitiesClass", "weeklySchedule[]");
        fieldPrefixKey = "applicantEducationSchedule";
    }

}