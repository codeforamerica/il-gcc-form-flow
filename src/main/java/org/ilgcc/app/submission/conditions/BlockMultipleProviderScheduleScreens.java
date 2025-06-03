package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BlockMultipleProviderScheduleScreens implements Condition {

    @Value("${il-gcc.enable-multiple-providers}")
    private boolean enableMultipleProviders;
    private final boolean allowScheduleScreensNavigation = false;

    @Override
    public Boolean run(Submission submission) {

        return enableMultipleProviders && allowScheduleScreensNavigation;
    }
    
    @Override
    public Boolean run(Submission submission, String uuid) {
        return enableMultipleProviders && allowScheduleScreensNavigation;
    }
}
