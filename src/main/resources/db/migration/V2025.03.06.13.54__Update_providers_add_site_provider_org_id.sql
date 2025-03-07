-- Add the 'site_provider_org_id' column to the 'providers' table
ALTER TABLE providers ADD COLUMN site_provider_org_id BIGINT;

-- Remove the existing foreign key constraint that uses resource_org_id
ALTER TABLE providers
DROP CONSTRAINT fk_providers_resource_org;

-- Add the new foreign key constraint on the providers table that uses site_provider_org_id
ALTER TABLE providers
    ADD CONSTRAINT fk_providers_resource_org
        FOREIGN KEY (site_provider_org_id) REFERENCES resource_organizations(resource_org_id)
            ON DELETE CASCADE;
