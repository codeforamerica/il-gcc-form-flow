
COPY childcare_providers (name, street_address_1, street_address_2, city, state, zipcode, ccms_id) FROM '.../src/main/resources/Provider_Info_Pilot_Phase_1.csv' DELIMITER ',' CSV HEADER