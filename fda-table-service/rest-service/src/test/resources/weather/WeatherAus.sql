/* https://www.kaggle.com/jsphyg/weather-dataset-rattle-package */
CREATE TABLE weather_aus
(
    mdb_id       BIGINT           NOT NULL PRIMARY KEY,
    mdb_date     DATE             NOT NULL,
    mdb_mintemp  DOUBLE PRECISION NULL,
    mdb_location VARCHAR(255)     NULL,
    mdb_rainfall DOUBLE PRECISION NULL
) WITH SYSTEM VERSIONING;

INSERT INTO weather_aus (mdb_id, mdb_date, mdb_location, mdb_mintemp, mdb_rainfall)
VALUES (1, '2008-12-01'::DATE, 13.4, 'Albury', 0.6),
       (2, '2008-12-02'::DATE, 7.4, 'Albury', 0),
       (3, '2008-12-03'::DATE, 12.9, 'Albury', 0);