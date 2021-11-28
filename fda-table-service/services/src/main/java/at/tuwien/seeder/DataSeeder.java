package at.tuwien.seeder;

import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DataSeeder implements Seeder {

    private final static Long TABLE_1_ID = 1L;
    private final static Table TABLE_1 = Table.builder()
            .id(TABLE_1_ID)
            .build();
    private final static Long DATABASE_1_ID = 1L;
    private final static Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .tables(List.of(TABLE_1))
            .build();

    private final static Long TABLE_2_ID = 2L;
    private final static Table TABLE_2 = Table.builder()
            .id(TABLE_2_ID)
            .build();

    private final static Long DATABASE_2_ID = 2L;
    private final static Database DATABASE_2 = Database.builder()
            .id(DATABASE_2_ID)
            .tables(List.of(TABLE_2))
            .build();

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

    private final DataService dataService;
    private final TableRepository tableRepository;

    @Autowired
    public DataSeeder(DataService dataService, TableRepository tableRepository) {
        this.dataService = dataService;
        this.tableRepository = tableRepository;
    }

    @Override
    public void seed() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, FileStorageException {
        if (tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID).isPresent()) {
            return;
        }
        dataService.insertCsv(DATABASE_1_ID, TABLE_1_ID, TABLE_1_INSERT);
        log.debug("seeded table {}", TABLE_1_ID);
        log.info("Seeded table {}", TABLE_1_ID);
        dataService.insertCsv(DATABASE_2_ID, TABLE_2_ID, TABLE_2_INSERT);
        log.debug("seeded table {}", TABLE_2_ID);
        log.info("Seeded table {}", TABLE_2_ID);
    }

}
