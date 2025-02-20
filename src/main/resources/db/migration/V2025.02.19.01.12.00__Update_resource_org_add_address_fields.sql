-- Add the address field columns to the 'resource_organizations' table
ALTER TABLE resource_organizations ADD COLUMN street_address VARCHAR;
ALTER TABLE resource_organizations ADD COLUMN city VARCHAR;
ALTER TABLE resource_organizations ADD COLUMN state VARCHAR;
ALTER TABLE resource_organizations ADD COLUMN zip_code VARCHAR;

-- Drop the address column from the 'resource_organizations' table
ALTER TABLE resource_organizations DROP COLUMN address;
