DO
$$
    BEGIN
        -- Create 'transactions' table if it does not exist
        IF NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'transactions') THEN
            CREATE TABLE transactions
            (
                transaction_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                work_item_id   UUID,
                submission_id UUID REFERENCES submissions (id) ON DELETE CASCADE NOT NULL,
                created_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
                updated_at     TIMESTAMP        DEFAULT CURRENT_TIMESTAMP NOT NULL,
                CONSTRAINT fk_submission FOREIGN KEY (submission_id) REFERENCES submissions (id) ON DELETE CASCADE
            );
        END IF;
    END
$$;