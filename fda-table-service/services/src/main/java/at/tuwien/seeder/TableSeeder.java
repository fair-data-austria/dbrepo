package at.tuwien.seeder;

import at.tuwien.api.database.DatabaseCreateDto;
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

    private final static TableCreateDto TABLE_1_CREATE = TableCreateDto.builder()
            .name(TABLE_1_NAME)
            .description(TABLE_1_DESCRIPTION)
            .columns(new ColumnCreateDto[]{
                    ColumnCreateDto.builder()
                            .name("Date")
                            .type(ColumnTypeDto.DATE)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Location")
                            .type(ColumnTypeDto.STRING)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("MinTemp")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("MaxTemp")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
                            .primaryKey(false)
                            .checkExpression(null)
                            .build(),
                    ColumnCreateDto.builder()
                            .name("Rainfall")
                            .type(ColumnTypeDto.NUMBER)
                            .unique(false)
                            .nullAllowed(false)
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
                            .nullAllowed(false)
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
    }

}
