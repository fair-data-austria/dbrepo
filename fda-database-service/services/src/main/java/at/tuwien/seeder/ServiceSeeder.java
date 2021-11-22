package at.tuwien.seeder;

import at.tuwien.exception.AmqpException;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DatabaseMalformedException;
import at.tuwien.exception.ImageNotSupportedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
@Profile("seeder,sandbox")
public class ServiceSeeder implements Seeder {

    private final DatabaseSeeder databaseSeeder;

    @Autowired
    public ServiceSeeder(DatabaseSeeder databaseSeeder) {
        this.databaseSeeder = databaseSeeder;
    }

    @Override
    @PostConstruct
    public void seed() throws ImageNotSupportedException, AmqpException, ContainerNotFoundException,
            DatabaseMalformedException {
        databaseSeeder.seed();
    }

}
