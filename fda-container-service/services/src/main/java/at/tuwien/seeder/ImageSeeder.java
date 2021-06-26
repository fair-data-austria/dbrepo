package at.tuwien.seeder;

import at.tuwien.api.container.image.ImageCreateDto;
import at.tuwien.api.container.image.ImageEnvItemDto;
import at.tuwien.api.container.image.ImageEnvItemTypeDto;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.service.ImageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Profile("seeder")
@Log4j2
public class ImageSeeder implements Seeder {

    private final static String IMAGE_1_REPOSITORY = "postgres";
    private final static String IMAGE_1_TAG = "13-alpine";
    private final static Integer IMAGE_1_PORT = 5432;
    private final static String IMAGE_1_DIALECT = "Postgres";
    private final static String IMAGE_1_DRIVER = "org.postgresql.Driver";
    private final static String IMAGE_1_JDBC = "postgresql";

    private final static String IMAGE_2_REPOSITORY = "mariadb";
    private final static String IMAGE_2_TAG = "latest";
    private final static Integer IMAGE_2_PORT = 3306;
    private final static String IMAGE_2_DIALECT = "MariaDB";
    private final static String IMAGE_2_DRIVER = "org.mariadb.jdbc.Driver";
    private final static String IMAGE_2_JDBC = "mariadb";

    private final static ImageEnvItemDto[] IMAGE_1_ENVIRONMENT = List.of(ImageEnvItemDto.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .type(ImageEnvItemTypeDto.USERNAME)
                    .build(),
            ImageEnvItemDto.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .type(ImageEnvItemTypeDto.PASSWORD)
                    .build())
            .toArray(new ImageEnvItemDto[0]);

    private final static ImageEnvItemDto[] IMAGE_2_ENVIRONMENT = List.of(ImageEnvItemDto.builder()
                    .key("MARIADB_USER")
                    .value("mysql")
                    .type(ImageEnvItemTypeDto.USERNAME)
                    .build(),
            ImageEnvItemDto.builder()
                    .key("MARIADB_PASSWORD")
                    .value("mysql")
                    .type(ImageEnvItemTypeDto.PASSWORD)
                    .build())
            .toArray(new ImageEnvItemDto[0]);

    private final static ImageCreateDto IMAGE_1_CREATE_DTO = ImageCreateDto.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .defaultPort(IMAGE_1_PORT)
            .dialect(IMAGE_1_DIALECT)
            .driverClass(IMAGE_1_DRIVER)
            .jdbcMethod(IMAGE_1_JDBC)
            .environment(IMAGE_1_ENVIRONMENT)
            .build();

    private final static ImageCreateDto IMAGE_2_CREATE_DTO = ImageCreateDto.builder()
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .defaultPort(IMAGE_2_PORT)
            .dialect(IMAGE_2_DIALECT)
            .driverClass(IMAGE_2_DRIVER)
            .jdbcMethod(IMAGE_2_JDBC)
            .environment(IMAGE_2_ENVIRONMENT)
            .build();

    private final ImageService imageService;

    @Autowired
    public ImageSeeder(ImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    @PostConstruct
    public void seed() throws DockerClientException, ImageNotFoundException {
        log.debug("seeded image {}", imageService.create(IMAGE_1_CREATE_DTO));
        log.debug("seeded image {}", imageService.create(IMAGE_2_CREATE_DTO));
    }

}
