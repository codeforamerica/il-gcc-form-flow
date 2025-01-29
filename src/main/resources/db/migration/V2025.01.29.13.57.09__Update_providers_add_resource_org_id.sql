-- Add the 'resource_org_id' column to the 'providers' table
ALTER TABLE providers ADD COLUMN resource_org_id BIGINT;

-- Add foreign key constraint on the providers table
ALTER TABLE providers
    ADD CONSTRAINT fk_providers_resource_org
        FOREIGN KEY (resource_org_id) REFERENCES resource_organizations(resource_org_id)
            ON DELETE CASCADE;
