DO
$$
BEGIN
        -- Create 'resource_organizations' table if it does not exist
        IF
NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'resource_organizations') THEN
CREATE TABLE resource_organizations
(
    resource_org_id BIGINT PRIMARY KEY,
    caseload_code   VARCHAR,
    name            VARCHAR,
    address         VARCHAR,
    phone           VARCHAR,
    email           VARCHAR,
    sda             SMALLINT
);

END IF;


        -- Create 'zip_codes' table if it does not exist
        IF
NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'zip_codes') THEN
CREATE TABLE zip_codes
(
    zip_code         BIGINT PRIMARY KEY,
    city             VARCHAR,
    county           VARCHAR,
    fips_county_code INT,
    dpa_county_code  INT,
    caseload_code    VARCHAR
);

END IF;

-- Create indexes for case load and county
CREATE INDEX caseload_code_idx ON resource_organizations (caseload_code);
CREATE INDEX county_idx ON zip_codes (county);

END
$$;