/* https://www.kaggle.com/jsphyg/weather-dataset-rattle-package */
CREATE TABLE weather_aus
(
    id       BIGINT           NOT NULL PRIMARY KEY,
    date     DATE             NOT NULL,
    location VARCHAR(255)     NULL,
    mintemp  DOUBLE PRECISION NULL,
    rainfall DOUBLE PRECISION NULL
) WITH SYSTEM VERSIONING;

INSERT INTO weather_aus (id, date, location, mintemp, rainfall)
VALUES (1, '2008-12-01'::DATE, 'Albury', 13.4,  0.6),
       (2, '2008-12-02'::DATE, 'Albury', 7.4, 0),
       (3, '2008-12-03'::DATE, 'Albury', 12.9, 0);