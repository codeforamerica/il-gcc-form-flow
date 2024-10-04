DO
$$
    BEGIN
        -- Create 'transmissions' table if it does not exist
        IF NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'transmissions') THEN
            CREATE TABLE transmissions
            (
                transmission_id     uuid PRIMARY KEY         DEFAULT gen_random_uuid(),
                submission_id       uuid REFERENCES submissions (id) ON DELETE CASCADE,
                user_file_id        uuid REFERENCES user_files (file_id) ON DELETE SET NULL,
                time_sent           TIMESTAMP WITH TIME ZONE NULL, -- Will Be Chicago Time
                status VARCHAR(20),
                type   VARCHAR(20),
                retry_attempts INTEGER,
                errors              JSONB
            );
        END IF;

        -- Check if transmission table is empty before generating transmissions for existing submissions
        IF (SELECT COUNT(*) FROM transmissions) = 0 THEN
            -- Insert transmission for each existing submissions' application PDF
            INSERT INTO transmissions (submission_id, status, type,
                                       time_sent)
            SELECT id, 'Complete', 'Application PDF', now()
            FROM submissions
            WHERE submitted_at IS NOT NULL;

            -- Insert transmission for each user file related to the submissions
            INSERT INTO transmissions (submission_id, user_file_id, status,
                                       type, time_sent)
            SELECT s.id, uf.file_id, 'Complete', 'Uploaded Document', now()
            FROM submissions s
                     JOIN user_files uf ON uf.submission_id = s.id
            WHERE s.submitted_at IS NOT NULL;
        END IF;
    END
$$;