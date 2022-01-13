package at.tuwien.seeder;

import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.repository.jpa.ImageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
public class Seeder {

    private final ImageRepository imageRepository;

    private final static Long IMAGE_MARIADB_ID = 1L;
    private final static String IMAGE_MARIADB_REPOSITORY = "mariadb";
    private final static String IMAGE_MARIADB_TAG = "10.5";
    private final static String IMAGE_MARIADB_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    private final static String IMAGE_MARIADB_DRIVER = "";
    private final static String IMAGE_MARIADB_JDBC = "mariadb";
    private final static String IMAGE_MARIADB_LOGO = "";
    private final static Integer IMAGE_MARIADB_PORT = 3306;

    private final static List<ContainerImageEnvironmentItem> IMAGE_MARIADB_ENVIRONMENT = List.of(
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_MARIADB_ID)
                    .key("UZERNAME")
                    .value("root")
                    .type(ContainerImageEnvironmentItemType.PRIVILEGED_USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_MARIADB_ID)
                    .key("MARIADB_ROOT_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PRIVILEGED_PASSWORD)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_MARIADB_ID)
                    .key("MARIADB_USER")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_MARIADB_ID)
                    .key("MARIADB_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .build());

    private final static ContainerImage IMAGE_MARIADB = ContainerImage.builder()
            .dialect(IMAGE_MARIADB_DIALECT)
            .driverClass(IMAGE_MARIADB_DRIVER)
            .jdbcMethod(IMAGE_MARIADB_JDBC)
            .logo(IMAGE_MARIADB_LOGO)
            .repository(IMAGE_MARIADB_REPOSITORY)
            .tag(IMAGE_MARIADB_TAG)
            .environment(IMAGE_MARIADB_ENVIRONMENT)
            .defaultPort(IMAGE_MARIADB_PORT)
            .build();

    @Autowired
    public Seeder(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public void seed() {
        if (imageRepository.existsById(IMAGE_MARIADB_ID)) {
            log.warn("Already seeded. Skip.");
            return;
        }
        final ContainerImage imageMariaDb = imageRepository.save(IMAGE_MARIADB);
        log.info("Seeded image {}", imageMariaDb.getId());
        log.debug("seeded imge {}", imageMariaDb);
    }

}
