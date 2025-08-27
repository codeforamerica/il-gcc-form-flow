-- Adding created_at and updated_at columns to providers
ALTER TABLE providers
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- Adding created_at and updated_at columns to resource_organizations
ALTER TABLE resource_organizations
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();

-- Adding created_at and updated_at columns to zip_codes
ALTER TABLE zip_codes
    ADD COLUMN created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW();