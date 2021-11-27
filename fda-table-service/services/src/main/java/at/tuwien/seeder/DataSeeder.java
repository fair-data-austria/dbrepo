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

    private final static TableInsertDto TABLE_1_INSERT = TableInsertDto.builder()
            .csvLocation("test:seed/weather_aus_small.csv")
            .nullElement("NA")
            .skipHeader(true)
            .delimiter(',')
            .build();

    private final DataService dataService;

    @Autowired
    public DataSeeder(DataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public void seed() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, FileStorageException {
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, TABLE_1_INSERT);
        log.debug("seeded table {}", TABLE_1_ID);
        log.info("Seeded table {}", TABLE_1_ID);
    }

}
