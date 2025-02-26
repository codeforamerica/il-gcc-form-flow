package org.ilgcc.app.submission.router;

import lombok.extern.slf4j.Slf4j;
import org.ilgcc.app.utils.enums.CCRRSlug;


@Slf4j
public class CCRR {
    public static String getCCRRSlugByOrganizationId(String organizationId) {
        for (CCRRSlug ccrrSlug : CCRRSlug.values()) {
            if (ccrrSlug.getOrgId().equals(organizationId)){
                return ccrrSlug.getValue();
            }
        }
        return CCRRSlug.FOUR_C.getValue();
    }
    
}
