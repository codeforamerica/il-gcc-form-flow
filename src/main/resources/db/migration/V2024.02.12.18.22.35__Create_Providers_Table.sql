CREATE TABLE IF NOT EXISTS childcare_providers(
    name VARCHAR NOT NULL,
    street_address_1 VARCHAR NOT NULL,
    street_address_2 VARCHAR,
    city VARCHAR NOT NULL,
    state VARCHAR NOT NULL,
    zipcode VARCHAR NOT NULL,
    ccms_id bigint NOT NUll,
    PRIMARY KEY (ccms_id)
);
