-- V20250918__update_timestamp_columns.sql
ALTER TABLE transactions
ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE
  USING created_at AT TIME ZONE 'UTC';

ALTER TABLE transactions
ALTER COLUMN updated_at TYPE TIMESTAMP WITH TIME ZONE
  USING updated_at AT TIME ZONE 'UTC';