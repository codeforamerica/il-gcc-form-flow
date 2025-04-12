package org.ilgcc.app.data;

import java.time.OffsetDateTime;
import lombok.Getter;

@Getter
public class ResourceOrganizationTransaction {
    private String organizationId;
    private String shortCode;
    private OffsetDateTime createdAt;
    private String workItemId;

    public ResourceOrganizationTransaction(String organizationId, OffsetDateTime createdAt, String shortCode, String workItemId) {
        this.organizationId = organizationId;
        this.workItemId = workItemId;
        this.shortCode = shortCode;
        this.createdAt = createdAt;
    }

}
