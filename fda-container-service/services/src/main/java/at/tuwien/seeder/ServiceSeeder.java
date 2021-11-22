package at.tuwien.seeder;

import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
@Profile("seed")
public class ServiceSeeder implements Seeder {

    private final ImageSeeder imageSeeder;
    private final ContainerSeeder containerSeeder;

    @Autowired
    public ServiceSeeder(ImageSeeder imageSeeder, ContainerSeeder containerSeeder) {
        this.imageSeeder = imageSeeder;
        this.containerSeeder = containerSeeder;
    }

    @Override
    @PostConstruct
    public void seed() throws DockerClientException, ImageNotFoundException {
        imageSeeder.seed();
        containerSeeder.seed();
    }
}
