DO
$$
    BEGIN
        -- Add transaction_type column if it doesn't exist
        IF NOT EXISTS (
            SELECT 1 FROM information_schema.columns
            WHERE table_name = 'transactions'
              AND column_name = 'transaction_type'
        ) THEN
            -- Add column as nullable first
            ALTER TABLE transactions
                ADD COLUMN transaction_type TEXT;

            -- Backfill existing rows with APPLICATION
            UPDATE transactions
            SET transaction_type = 'application'
            WHERE transaction_type IS NULL;

            -- Now make the column NOT NULL
            ALTER TABLE transactions
                ALTER COLUMN transaction_type SET NOT NULL;
        END IF;
    END
$$;