package at.tuwien;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.database.Database;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.concurrent.TimeUnit;


@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseIntegrationTest {

    public final Long IMAGE_1_ID = 1L;
    public final String IMAGE_1_REPO = "postgres";
    public final String IMAGE_1_TAG = "latest";
    public final Instant IMAGE_1_CREATED = Instant.now();
    public final Instant IMAGE_1_UPDATED = Instant.now();

    public final Long DATABASE_1_ID = 1L;
    public final String DATABASE_1_NAME = "Fundamentals SEC";
    public final String DATABASE_1_INTERNALNAME = "fundamentals-sec";
    public final String DATABASE_1_IMAGE = "postgres";
    public final Instant DATABASE_1_CREATED = Instant.now();
    public final Instant DATABASE_1_UPDATED = Instant.now();

    public final Long CONTAINER_1_ID = 1L;
    public final String CONTAINER_1_NAME = "fda-userdb-fundamentals-sec";
    public final Instant CONTAINER_1_CREATED = Instant.now();
    public final Instant CONTAINER_1_UPDATED = Instant.now();

    public final ContainerImage IMAGE_1 = ContainerImage.builder()
            .id(IMAGE_1_ID)
            .repository(IMAGE_1_REPO)
            .tag(IMAGE_1_TAG)
            .created(IMAGE_1_CREATED)
            .compiled(IMAGE_1_UPDATED)
            .build();

    public final Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_NAME)
            .containerCreated(CONTAINER_1_CREATED)
            .lastModified(CONTAINER_1_UPDATED)
            .image(IMAGE_1)
            .build();

    public final Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .name(DATABASE_1_NAME)
            .internalName(DATABASE_1_INTERNALNAME)
            .created(DATABASE_1_CREATED)
            .lastModified(DATABASE_1_UPDATED)
            .container(CONTAINER_1)
            .build();

}
