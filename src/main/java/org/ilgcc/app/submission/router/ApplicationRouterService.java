package org.ilgcc.app.submission.router;

import formflow.library.data.SubmissionRepositoryService;
import java.util.Optional;
import org.ilgcc.app.utils.ZipcodeOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public interface ApplicationRouterService {


    /**
     * Returns the organization ID for a given ZIP code
     * @param zipCode The ZIP code to look up
     * @return The corresponding organization ID
     */
    static Optional<String> getOrganizationIdByZipCode(String zipCode){
        final String truncatedZip = zipCode.substring(0, 5);
        return ZipcodeOption.getOrganizationIdByZipCode(truncatedZip);
    };

    /**
     * Returns the organization ID for a given provider ID
     * @param providerId The alphanumeric provider ID to look up
     * @return The corresponding organization ID
     */
    Optional<String> getOrganizationIdByProviderId(String providerId);
}
