package at.tuwien;

import at.tuwien.api.container.image.ImageEnvItemDto;
import at.tuwien.api.container.image.ImageEnvItemTypeDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.query.Query;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.test.context.TestPropertySource;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.MINUTES;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "postgres";
    public final static String IMAGE_1_TAG = "13-alpine";
    public final static String IMAGE_1_HASH = "83b40f2726e5";
    public final static Integer IMAGE_1_PORT = 5432;
    public final static String IMAGE_1_DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
    public final static String IMAGE_1_DRIVER = "org.postgresql.Driver";
    public final static String IMAGE_1_JDBC = "postgresql";
    public final static Long IMAGE_1_SIZE = 12000L;
    public final static String IMAGE_1_LOGO = "AAAA";
    public final static Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);
    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .build());
    public final static ImageEnvItemDto[] IMAGE_1_ENV_DTO = List.of(ImageEnvItemDto.builder()
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
    public final static List<String> IMAGE_1_ENVIRONMENT = List.of("POSTGRES_USER=postgres",
            "POSTGRES_PASSWORD=postgres");

    public final Long DATABASE_1_ID = 1L;
    public final String DATABASE_1_NAME = "Fundamentals SEC";
    public final String DATABASE_1_INTERNALNAME = "fundamentals_sec";
    public final String DATABASE_1_IMAGE = "postgres:latest";
    public final Instant DATABASE_1_CREATED = Instant.now();
    public final Instant DATABASE_1_UPDATED = Instant.now();

    public final Long CONTAINER_1_ID = 1L;
    public final String CONTAINER_1_NAME = "fda-userdb-fundamentals-sec";
    public final Instant CONTAINER_1_CREATED = Instant.now();
    public final Instant CONTAINER_1_UPDATED = Instant.now();

    public final Long DATABASE_2_ID = 2L;
    public final String DATABASE_2_NAME = "Motorola";
    public final String DATABASE_2_INTERNALNAME = "motorola";
    public final String DATABASE_2_IMAGE = "mariadb:latest"; /* for whatever reason, should not be there in the first place */
    public final Instant DATABASE_2_CREATED = Instant.now();
    public final Instant DATABASE_2_UPDATED = Instant.now();

    public final Long CONTAINER_2_ID = 2L;
    public final String CONTAINER_2_NAME = "fda-userdb-motorola";
    public final Instant CONTAINER_2_CREATED = Instant.now();
    public final Instant CONTAINER_2_UPDATED = Instant.now();

    public final static Long QUERY_1_ID = 1L;
    public final static String QUERY_1_STATEMENT = "SELECT * FROM table;";
    public final static String QUERY_1_NORMALIZED = "SELECT * FROM table;";
    public final static String QUERY_1_HASH = DigestUtils.sha1Hex(QUERY_1_STATEMENT);
    public final static Instant QUERY_1_CREATED = Instant.now().minus(1, MINUTES);
    public final static Instant QUERY_1_UPDATED= Instant.now();
    public final static Timestamp QUERY_1_TIMESTAMP = Timestamp.from(Instant.now().minus(1, MINUTES));
    public final static Integer QUERY_1_RESULTNUMBER = 1;
    public final static String QUERY_1_RESULTHASH = DigestUtils.sha1Hex("c00lr3su1t");

    public final static ContainerImage IMAGE_1 = ContainerImage.builder()
            .id(IMAGE_1_ID)
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .jdbcMethod(IMAGE_1_JDBC)
            .dialect(IMAGE_1_DIALECT)
            .driverClass(IMAGE_1_DRIVER)
            .containers(List.of())
            .compiled(IMAGE_1_BUILT)
            .size(IMAGE_1_SIZE)
            .environment(IMAGE_1_ENV)
            .defaultPort(IMAGE_1_PORT)
            .logo(IMAGE_1_LOGO)
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

    public final Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .internalName(CONTAINER_2_NAME)
            .containerCreated(CONTAINER_2_CREATED)
            .lastModified(CONTAINER_2_UPDATED)
            .image(IMAGE_1)
            .build();

    public final Database DATABASE_2 = Database.builder()
            .id(DATABASE_2_ID)
            .name(DATABASE_2_NAME)
            .internalName(DATABASE_2_INTERNALNAME)
            .created(DATABASE_2_CREATED)
            .lastModified(DATABASE_2_UPDATED)
            .container(CONTAINER_2)
            .build();

}
