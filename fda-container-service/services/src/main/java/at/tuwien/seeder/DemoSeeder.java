package at.tuwien.seeder;

import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;

@Component
@Deprecated
@Log4j2
public class DemoSeeder {

    private final ImageRepository imageRepository;

    @Autowired
    public DemoSeeder(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void seeder() {
        final ContainerImage postgres = ContainerImage.builder()
                .defaultPort(5432)
                .repository("postgres")
                .compiled(Instant.now())
                .hash("deadbeef")
                .size(1L)
                .tag("latest")
                .environment(List.of(ContainerImageEnvironmentItem.builder()
                                .key("POSTGRES_USER")
                                .value("postgres")
                                .build(),
                        ContainerImageEnvironmentItem.builder()
                                .key("POSTGRES_PASSWORD")
                                .value("postgres")
                                .build()))
                .build();
        if (imageRepository.findAll().size() == 0) {
            final ContainerImage image = imageRepository.save(postgres);
            log.info("Seeded {}:{} image", image.getRepository(), image.getTag());
            log.debug("seeded image {}", image);
        }
    }

}
