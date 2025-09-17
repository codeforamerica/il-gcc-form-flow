DO
$$
    BEGIN
        -- Create 'UserFileTransaction' table if it does not exist
        IF NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'user_file_transactions') THEN
            CREATE TABLE user_file_transactions
            (
                transaction_file_id UUID PRIMARY KEY NOT NULL,
                user_file_id UUID REFERENCES user_files (file_id) ON DELETE CASCADE NOT NULL,
                transaction_status TEXT NOT NULL,
                transaction_id UUID REFERENCES transactions (transaction_id) ON DELETE CASCADE NOT NULL,
                submission_id UUID REFERENCES submissions (id) ON DELETE CASCADE NOT NULL,
                created_at     TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP NOT NULL,
                updated_at     TIMESTAMPTZ        DEFAULT CURRENT_TIMESTAMP NOT NULL,
                UNIQUE (user_file_id)
            );

            CREATE INDEX idx_uft_transaction   ON user_file_transactions (transaction_id);
            CREATE INDEX idx_uft_submission    ON user_file_transactions (submission_id);
            CREATE INDEX idx_uft_status        ON user_file_transactions (transaction_status);
            CREATE INDEX idx_uft_tx_status ON user_file_transactions (transaction_id, transaction_status);

        END IF;
    END
$$;