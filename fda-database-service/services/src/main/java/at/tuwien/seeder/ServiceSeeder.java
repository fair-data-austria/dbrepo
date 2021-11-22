package at.tuwien.seeder;

import at.tuwien.exception.AmqpException;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.exception.ImageNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Slf4j
@Component
@Profile("seeder")
public class ServiceSeeder implements Seeder {

    private final DatabaseSeeder databaseSeeder;
    private final Environment environment;

    @Autowired
    public ServiceSeeder(DatabaseSeeder databaseSeeder, Environment environment) {
        this.databaseSeeder = databaseSeeder;
        this.environment = environment;
    }

    @Override
    @PostConstruct
    public void seed() throws ImageNotSupportedException, AmqpException, ContainerNotFoundException,
            DatabaseMalformedException {
        if (Arrays.asList(environment.getActiveProfiles()).contains("sandbox")) {
            databaseSeeder.seed();
        }
    }

}
