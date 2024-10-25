DO
$$
    BEGIN
        -- Create 'providers' table if it does not exist
        IF NOT EXISTS (SELECT * FROM pg_tables WHERE tablename = 'providers') THEN
            CREATE TABLE providers
            (
                provider_id BIGINT PRIMARY KEY,
                type VARCHAR,
                name VARCHAR,
                dba_name VARCHAR,
                street_address VARCHAR,
                city VARCHAR,
                state VARCHAR,
                zip_code VARCHAR,
                status VARCHAR
            );
        END IF;
    END
$$;