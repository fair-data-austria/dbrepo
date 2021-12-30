package at.tuwien.seeder;

import at.tuwien.exception.*;
import com.google.common.io.Files;
import com.opencsv.exceptions.CsvException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@Component
@Profile("seeder")
public class ServiceSeeder implements Seeder {

    @Value("${fda.ready.path}")
    private String readyPath;

    private final DataSeeder dataSeeder;
    private final TableSeeder tableSeeder;
    private final Environment environment;

    @Autowired
    public ServiceSeeder(DataSeeder dataSeeder, TableSeeder tableSeeder, Environment environment) {
        this.dataSeeder = dataSeeder;
        this.tableSeeder = tableSeeder;
        this.environment = environment;
    }

    @Override
    @PostConstruct
    public void seed() throws TableMalformedException, ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableNotFoundException,
            IOException, CsvException {
        if (Arrays.asList(environment.getActiveProfiles()).contains("sandbox")) {
            tableSeeder.seed();
            dataSeeder.seed();
        }
        log.info("Seeding completed, service is ready");
        Files.touch(new File(readyPath));
    }

}
