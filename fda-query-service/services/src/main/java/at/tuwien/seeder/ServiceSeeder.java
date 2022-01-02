package at.tuwien.seeder;

import at.tuwien.exception.*;
import com.google.common.io.Files;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    private final QuerySeeder querySeeder;
    private final Environment environment;

    @Autowired
    public ServiceSeeder(QuerySeeder querySeeder, Environment environment) {
        this.querySeeder = querySeeder;
        this.environment = environment;
    }

    @Override
    @Transactional
    @PostConstruct
    public void seed() throws IOException, QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {
        if (Arrays.asList(environment.getActiveProfiles()).contains("sandbox")) {
            querySeeder.seed();
        }
        log.info("Seeding completed, service is ready");
        Files.touch(new File(readyPath));
    }

}
