/* https://sandbox.zenodo.org/api/files/6aca3421-add3-489b-8c4a-35228fe5c683/species_id.csv */
CREATE TABLE maldi_ms_data
(
    qu      VARCHAR(255) NOT NULL,
    species VARCHAR(255) NOT NULL,
    score   VARCHAR(255) NOT NULL
) WITH SYSTEM VERSIONING;