package org.ilgcc.app.submission.actions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ValidateActivitiesJobSchedule extends ValidateHourlySchedule {

    public ValidateActivitiesJobSchedule(MessageSource messageSource) {
        super(messageSource, "activitiesJob");
    }
}
