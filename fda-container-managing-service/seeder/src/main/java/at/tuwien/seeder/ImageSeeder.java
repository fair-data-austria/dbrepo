package at.tuwien.seeder;

import at.tuwien.entity.Architecture;
import at.tuwien.entity.ContainerImage;
import at.tuwien.repository.ImageRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Log4j2
@Component
public class ImageSeeder {

    private final ImageRepository imageRepository;

    @Autowired
    public ImageSeeder(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @PostConstruct
    public void seed() {
        if (imageRepository.findAll().size() > 0) {
            log.info("Already seeded, skip.");
            return;
        }
        ContainerImage image1 = ContainerImage.builder()
                .repository("postgres")
                .tag("latest")
                .architecture(Architecture.LINUX_AMD64)
                .hash("fa3a9448e6b70b7c666c63b7f3b5a2015b670ef0eeb152a2552081843d156959")
                .build();
        ContainerImage image2 = ContainerImage.builder()
                .repository("postgres")
                .tag("12-alpine")
                .architecture(Architecture.LINUX_AMD64)
                .hash("acfc2eec2890ecd5ee7cae5464376703383421be4c40108c467f36740c84f47f")
                .build();
        ContainerImage image3 = ContainerImage.builder()
                .repository("postgres")
                .tag("12")
                .architecture(Architecture.LINUX_AMD64)
                .hash("bfab8d626892e59f4b66227b24014d0af18bda67c760d4e450b7a2e348fecb83")
                .build();
        final List<ContainerImage> images = List.of(image1, image2, image3);
        imageRepository.saveAll(images);
        log.info("Seeded {} images", images.size());
    }

}
