package at.tuwien.seeder;

import at.tuwien.exception.*;
import at.tuwien.service.DataService;
import at.tuwien.service.FileService;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class DataSeeder implements Seeder {

    private final static Long TABLE_1_ID = 1L;
    private final static Long DATABASE_1_ID = 1L;

    private final static Long TABLE_2_ID = 2L;
    private final static Long DATABASE_2_ID = 2L;

    private final static Long TABLE_3_ID = 3L;
    private final static Long DATABASE_3_ID = 3L;

    private final static String TABLE_1_INSERT = "test:seed/weather-small.csv";
    private final static String TABLE_2_INSERT = "test:seed/infection-small.csv";
    private final static String TABLE_3_INSERT = "test:seed/air-small.csv";

    private final DataService dataService;
    private final FileService fileService;

    @Autowired
    public DataSeeder(DataService dataService, FileService textDataService) {
        this.dataService = dataService;
        this.fileService = textDataService;
    }

    @Override
    public void seed() throws TableNotFoundException, TableMalformedException, DatabaseNotFoundException,
            ImageNotSupportedException, IOException, CsvException {
        dataService.insert(DATABASE_1_ID, TABLE_1_ID, fileService.read(DATABASE_1_ID, TABLE_1_ID, TABLE_1_INSERT));
        log.info("Seeded table {}", TABLE_1_ID);
        dataService.insert(DATABASE_2_ID, TABLE_2_ID, fileService.read(DATABASE_2_ID, TABLE_2_ID, TABLE_2_INSERT));
        log.info("Seeded table {}", TABLE_2_ID);
        dataService.insert(DATABASE_3_ID, TABLE_3_ID, fileService.read(DATABASE_3_ID, TABLE_3_ID, TABLE_3_INSERT));
        log.info("Seeded table {}", TABLE_3_ID);
    }

}
