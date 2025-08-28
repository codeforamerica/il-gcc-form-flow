-- Because we're not always updating providers, resource_organizations, or zip_codes using
-- Hibernate/application code, we need to ensure the updated_at columns change on raw SQL
-- UPDATE commands

-- Create a function that will update the updated_at column on a table
CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
  NEW.updated_at = now();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Set a trigger for providers to call the set_updated_at function on an update
DROP TRIGGER IF EXISTS providers_set_updated_at ON providers;
CREATE TRIGGER providers_set_updated_at
    BEFORE UPDATE ON providers
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Set a trigger for resource_organizations to call the set_updated_at function on an update
DROP TRIGGER IF EXISTS resource_orgs_set_updated_at ON resource_organizations;
CREATE TRIGGER resource_orgs_set_updated_at
    BEFORE UPDATE ON resource_organizations
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

-- Set a trigger for zip_codes to call the set_updated_at function on an update
DROP TRIGGER IF EXISTS zip_codes_set_updated_at ON zip_codes;
CREATE TRIGGER zip_codes_set_updated_at
    BEFORE UPDATE ON zip_codes
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();