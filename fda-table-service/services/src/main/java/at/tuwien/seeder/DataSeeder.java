package at.tuwien.seeder;

import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.exception.*;
import at.tuwien.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataSeeder implements Seeder {

    private final static Long TABLE_1_ID = 1L;
    private final static Long DATABASE_1_ID = 1L;

    private final static Long TABLE_2_ID = 2L;
    private final static Long DATABASE_2_ID = 2L;

    private final static Long TABLE_3_ID = 3L;
    private final static Long DATABASE_3_ID = 3L;

    private final static TableInsertDto TABLE_1_INSERT = TableInsertDto.builder()
            .csvLocation("test:seed/weather-small.csv")
            .nullElement("NA")
            .skipHeader(true)
            .trueElement("Yes")
            .falseElement("No")
            .delimiter(',')
            .build();

    private final static TableInsertDto TABLE_2_INSERT = TableInsertDto.builder()
            .csvLocation("test:seed/infection-small.csv")
            .skipHeader(true)
            .delimiter(',')
            .build();

    private final static TableInsertDto TABLE_3_INSERT = TableInsertDto.builder()
            .csvLocation("test:seed/air-small.csv")
            .skipHeader(true)
            .delimiter(';')
            .build();

    private final DataService dataService;

    @Autowired
    public DataSeeder(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void seed() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException {
        dataService.insert(DATABASE_1_ID, TABLE_1_ID, TABLE_1_INSERT);
        log.info("Seeded table {}", TABLE_1_ID);
        dataService.insert(DATABASE_2_ID, TABLE_2_ID, TABLE_2_INSERT);
        log.info("Seeded table {}", TABLE_2_ID);
        dataService.insert(DATABASE_3_ID, TABLE_3_ID, TABLE_3_INSERT);
        log.info("Seeded table {}", TABLE_3_ID);
    }

}
