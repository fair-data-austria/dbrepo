package at.tuwien.seeder;

import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;

@Log4j2
@Component
@Profile("seeder")
public class ServiceSeeder implements Seeder {

    @Value("${fda.ready.path}")
    private String readyPath;

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
    @SneakyThrows
    @PostConstruct
    public void seed() {
        imageSeeder.seed();
        if (Arrays.asList(environment.getActiveProfiles()).contains("sandbox")) {
            containerSeeder.seed();
            Thread.sleep(10 * 1000);
            log.info("Seeding completed, service is ready");
            Files.touch(new File(readyPath));
        }
    }
}
