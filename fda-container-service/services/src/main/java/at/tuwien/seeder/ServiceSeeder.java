package at.tuwien.seeder;

import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@Log4j2
@Component
@Profile("seeder")
public class ServiceSeeder implements Seeder {

    private final ImageSeeder imageSeeder;
    private final ContainerSeeder containerSeeder;
    private final Environment environment;

    @Autowired
    public ServiceSeeder(ImageSeeder imageSeeder, ContainerSeeder containerSeeder, Environment environment) {
        this.imageSeeder = imageSeeder;
        this.containerSeeder = containerSeeder;
        this.environment = environment;
    }

    @Override
    @PostConstruct
    public void seed() throws DockerClientException, ImageNotFoundException {
        imageSeeder.seed();
        if (Arrays.asList(environment.getActiveProfiles()).contains("sandbox")) {
            containerSeeder.seed();
        }
    }
}
