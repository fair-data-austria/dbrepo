package at.tuwien.seeder;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.exception.*;
import at.tuwien.service.TableService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TableSeeder implements Seeder {

    private final static Long TABLE_1_ID = 1L;
    private final static Long TABLE_1_DATABASE_ID = 1L;
    private final static String TABLE_1_NAME = "Rain in Australia";
    private final static String TABLE_1_DESCRIPTION = "Predict next-day rain by training classification models on the target variable RainTomorrow";

    private final static Long TABLE_2_ID = 1L;
    private final static Long TABLE_2_DATABASE_ID = 2L;
    private final static String TABLE_2_NAME = "Infection COVID19";
    private final static String TABLE_2_DESCRIPTION = "Download historical data (to 14 December 2020) on the daily number of new reported COVID-19 cases and deaths worldwide";

    private final static Long TABLE_3_ID = 1L;
    private final static Long TABLE_3_DATABASE_ID = 3L;
    private final static String TABLE_3_NAME = "Air Quality AT";
    private final static String TABLE_3_DESCRIPTION = "World Air Quality (OpenAQ) for Austria 2016-2021";

    private final static TableCreateDto TABLE_1_CREATE = TableCreateDto.builder()
            .name(TABLE_1_NAME)
            .description(TABLE_1_DESCRIPTION)
            .columns(new ColumnCreateDto[]{
                    ColumnCreateDto.builder()
                            .name("Date")
                            .type(ColumnTypeDto.DATE)
                            .unique(true)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Location")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("MinTemp")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("MaxTemp")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Rainfall")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Evaporation")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Sunshine")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("WindGustDir")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("WindGustSpeed")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("WindDir9am")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("WindDir3pm")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("WindSpeed9am")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("WindSpeed3pm")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Humidity9am")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Humidity3pm")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Pressure9am")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Pressure3pm")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Cloud9am")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Cloud3pm")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Temp9am")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Temp3pm")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("RainToday")
                            .type(ColumnTypeDto.BOOLEAN)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("RainTomorrow")
                            .type(ColumnTypeDto.BOOLEAN)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build()
            })
            .build();

    private final static TableCreateDto TABLE_2_CREATE = TableCreateDto.builder()
            .name(TABLE_2_NAME)
            .description(TABLE_2_DESCRIPTION)
            .columns(new ColumnCreateDto[]{
                    ColumnCreateDto.builder()
                            .name("dateRep")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("day")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("month")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("year")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("cases")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("deaths")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("countriesAndTerritories")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("geoId")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("countryterritoryCode")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("popData2019")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("continentExp")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Cumulative_number_for_14_days_of_COVID_19_cases_per_100000")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build()
            })
            .build();

    private final static TableCreateDto TABLE_3_CREATE = TableCreateDto.builder()
            .name(TABLE_3_NAME)
            .description(TABLE_3_DESCRIPTION)
            .columns(new ColumnCreateDto[]{
                    ColumnCreateDto.builder()
                            .name("Country Code")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("City")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Location")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Coordinates")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Pollutant")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(true)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Source Name")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Unit")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Value")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Last Updated")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Country Label")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
            })
            .build();

    private final TableService tableService;

    @Autowired
    public TableSeeder(TableService tableService) {
        this.tableService = tableService;
    }

    @Override
    public void seed() throws TableMalformedException, ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException {
        log.debug("seeded table {}", tableService.createTable(TABLE_1_DATABASE_ID, TABLE_1_CREATE));
        log.info("Seeded table {}", TABLE_1_ID);
        log.debug("seeded table {}", tableService.createTable(TABLE_2_DATABASE_ID, TABLE_2_CREATE));
        log.info("Seeded table {}", TABLE_2_ID);
        log.debug("seeded table {}", tableService.createTable(TABLE_3_DATABASE_ID, TABLE_3_CREATE));
        log.info("Seeded table {}", TABLE_3_ID);
    }

}
