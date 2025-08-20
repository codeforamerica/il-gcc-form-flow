package org.ilgcc.app.submission.conditions;

import formflow.library.config.submission.Condition;
import formflow.library.data.Submission;
import java.util.Optional;
import org.ilgcc.app.data.ResourceOrganization;
import org.ilgcc.app.submission.router.ApplicationRoutingServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DoesNotHaveValidHomeAddressZipCode implements Condition {

    @Autowired
    ApplicationRoutingServiceImpl applicationRoutingService;

    @Override
    public Boolean run(Submission submission) {
        String parentHomeZipCode = submission.getInputData().getOrDefault("parentHomeZipCode", "").toString();
        if (parentHomeZipCode.isBlank() ) {
            return true;
        }
        Optional<ResourceOrganization> organizationIdByZipCode = applicationRoutingService.getOrganizationIdByZipCode(
                parentHomeZipCode);
        return organizationIdByZipCode.isEmpty();
    }
}
