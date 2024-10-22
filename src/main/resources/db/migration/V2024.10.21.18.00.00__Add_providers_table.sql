DO
$$
    BEGIN
        -- Create 'providers' table if it does not exist
        IF NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'providers') THEN
            CREATE TABLE providers
            (
                provider_id BIGINT PRIMARY KEY,
                type VARCHAR NOT NULL,
                name VARCHAR NOT NULL,
                dba_name VARCHAR,
                street_address VARCHAR NOT NULL,
                city VARCHAR NOT NULL,
                state VARCHAR NOT NULL,
                zip_code VARCHAR NOT NULL,
                status VARCHAR NOT NULL
            );
        END IF;
    END
$$;