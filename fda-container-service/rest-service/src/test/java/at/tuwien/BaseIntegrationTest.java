package at.tuwien;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseIntegrationTest {

    public final String IMAGE_1_REPOSITORY = "postgres";
    public final String IMAGE_1_TAG = "13-alpine";
    public final String IMAGE_1_HASH = "83b40f2726e5";
    public final Integer IMAGE_1_PORT = 5432;
    public final Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);
    public final List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build());

    public final String IMAGE_2_REPOSITORY = "redis";
    public final String IMAGE_2_TAG = "latest";
    public final String IMAGE_2_HASH = "f877e80bb9ef";
    public final Integer IMAGE_2_PORT = 6379;
    public final Instant IMAGE_2_BUILT = Instant.now().minus(9, DAYS);
    public final List<ContainerImageEnvironmentItem> IMAGE_2_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build());

    public final ContainerImage IMAGE_1 = ContainerImage.builder()
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .compiled(IMAGE_1_BUILT)
            .environment(IMAGE_1_ENV)
            .defaultPort(IMAGE_1_PORT)
            .build();

    public final ContainerImage IMAGE_2 = ContainerImage.builder()
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .hash(IMAGE_2_HASH)
            .compiled(IMAGE_2_BUILT)
            .environment(IMAGE_2_ENV)
            .defaultPort(IMAGE_2_PORT)
            .build();

    public final Long CONTAINER_1_ID = 1L;
    public final String CONTAINER_1_HASH = "deadbeef";
    public final ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final String CONTAINER_1_NAME = "u01";
    public final String CONTAINER_1_DATABASE = "univie";
    public final String CONTAINER_1_IP = "231.145.98.83";
    public final Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final Long CONTAINER_2_ID = 2L;
    public final String CONTAINER_2_HASH = "0ff1ce";
    public final ContainerImage CONTAINER_2_IMAGE = IMAGE_2;
    public final String CONTAINER_2_NAME = "t01";
    public final String CONTAINER_2_DATABASE = "tuw";
    public final String CONTAINER_2_IP = "233.145.99.83";
    public final Instant CONTAINER_2_CREATED = Instant.now().minus(1, HOURS);

    public final Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_1_HASH)
            .containerCreated(CONTAINER_1_CREATED)
            .build();

    public final Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .image(CONTAINER_2_IMAGE)
            .hash(CONTAINER_2_HASH)
            .containerCreated(CONTAINER_2_CREATED)
            .build();

}
