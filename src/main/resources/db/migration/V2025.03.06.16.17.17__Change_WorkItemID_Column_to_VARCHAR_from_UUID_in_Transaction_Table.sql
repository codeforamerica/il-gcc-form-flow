ALTER TABLE transactions
    ALTER COLUMN work_item_id TYPE VARCHAR(15) USING work_item_id::TEXT;
