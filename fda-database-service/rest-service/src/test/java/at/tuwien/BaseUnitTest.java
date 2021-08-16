package at.tuwien;

import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static String BROKER_NAME = "fda-broker-service";
    public final static String BROKER_IP = "172.29.0.2";
    public final static String BROKER_HOSTNAME = "fda-broker-service";
    public final static String BROKER_IMAGE = "rabbitmq";
    public final static String BROKER_TAG = "3-alpine";

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "postgres";
    public final static String IMAGE_1_TAG = "latest";
    public final static String IMAGE_1_HASH = "83b40f2726e5";
    public final static String IMAGE_1_DIALECT = "POSTGRES";
    public final static String IMAGE_1_DRIVER = "org.postgresql.Driver";
    public final static String IMAGE_1_JDBC = "postgresql";
    public final static String IMAGE_1_LOGO = "AAAA";
    public final static Integer IMAGE_1_PORT = 5432;
    public final static Long IMAGE_1_SIZE = 12000L;
    public final static Instant IMAGE_1_CREATED = Instant.now();
    public final static Instant IMAGE_1_UPDATED = Instant.now();
    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENVIRONMENT = List.of(ContainerImageEnvironmentItem.builder()
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build());

    public final static Long IMAGE_2_ID = 2L;
    public final static String IMAGE_2_REPOSITORY = "mariadb";
    public final static String IMAGE_2_TAG = "latest";
    public final static String IMAGE_2_HASH = "83b40f2726e6";
    public final static String IMAGE_2_DIALECT = "MARIADB";
    public final static String IMAGE_2_DRIVER = "org.mariadb.jdbc.Driver";
    public final static String IMAGE_2_JDBC = "mariadb";
    public final static String IMAGE_2_LOGO = "BBBB";
    public final static Integer IMAGE_2_PORT = 5432;
    public final static Long IMAGE_2_SIZE = 14000L;
    public final static Instant IMAGE_2_CREATED = Instant.now();
    public final static Instant IMAGE_2_UPDATED = Instant.now();
    public final static List<ContainerImageEnvironmentItem> IMAGE_2_ENVIRONMENT = List.of(ContainerImageEnvironmentItem.builder()
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .key("MARIADB_ROOT_PASSWORD")
                    .value("mariadb")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .key("UZERNAME")
                    .value("root")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .type(ContainerImageEnvironmentItemType.OTHER)
                    .key("MARIADB_USER")
                    .value("mariadb")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .type(ContainerImageEnvironmentItemType.OTHER)
                    .key("MARIADB_PASSWORD")
                    .value("mariadb")
                    .build());

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "Fundamentals SEC";
    public final static Boolean DATABASE_1_PUBLIC = false;
    public final static String DATABASE_1_INTERNALNAME = "sec";
    public final static String DATABASE_1_EXCHANGE = "fda." + DATABASE_1_INTERNALNAME;
    public final static Instant DATABASE_1_CREATED = Instant.now().minus(1, HOURS);
    public final static Instant DATABASE_1_UPDATED = Instant.now();

    public final static Long DATABASE_2_ID = 2L;
    public final static String DATABASE_2_NAME = "River Flow";
    public final static Boolean DATABASE_2_PUBLIC = false;
    public final static String DATABASE_2_INTERNALNAME = "river_flow";
    public final static String DATABASE_2_EXCHANGE = "fda." + DATABASE_2_INTERNALNAME;
    public final static Instant DATABASE_2_CREATED = Instant.now().minus(1, HOURS);
    public final static Instant DATABASE_2_UPDATED = Instant.now();

    public final static Long CONTAINER_1_ID = 1L;
    public final static String CONTAINER_1_HASH = "deadbeef";
    public final static String CONTAINER_1_NAME = "fda-userdb-u01";
    public final static String CONTAINER_1_INTERNALNAME = "fda-userdb-u01";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(2, HOURS);
    public final static Instant CONTAINER_1_UPDATED = Instant.now();
    public final static String CONTAINER_1_IP = "172.28.0.5";

    public final static Long CONTAINER_2_ID = 2L;
    public final static String CONTAINER_2_HASH = "0ff1ce";
    public final static String CONTAINER_2_NAME = "fda-userdb-u02";
    public final static String CONTAINER_2_INTERNALNAME = "fda-userdb-u02";
    public final static Instant CONTAINER_2_CREATED = Instant.now().minus(2, HOURS);
    public final static Instant CONTAINER_2_UPDATED = Instant.now();
    public final static String CONTAINER_2_IP = "172.28.0.6";

    public final static ContainerImage IMAGE_1 = ContainerImage.builder()
            .id(IMAGE_1_ID)
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .size(IMAGE_1_SIZE)
            .environment(IMAGE_1_ENVIRONMENT)
            .dialect(IMAGE_1_DIALECT)
            .driverClass(IMAGE_1_DRIVER)
            .jdbcMethod(IMAGE_1_JDBC)
            .created(IMAGE_1_CREATED)
            .defaultPort(IMAGE_1_PORT)
            .compiled(IMAGE_1_UPDATED)
            .logo(IMAGE_1_LOGO)
            .build();

    public final static ContainerImage IMAGE_2 = ContainerImage.builder()
            .id(IMAGE_2_ID)
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .hash(IMAGE_2_HASH)
            .size(IMAGE_2_SIZE)
            .environment(IMAGE_2_ENVIRONMENT)
            .dialect(IMAGE_2_DIALECT)
            .driverClass(IMAGE_2_DRIVER)
            .jdbcMethod(IMAGE_2_JDBC)
            .created(IMAGE_2_CREATED)
            .defaultPort(IMAGE_2_PORT)
            .compiled(IMAGE_2_UPDATED)
            .logo(IMAGE_2_LOGO)
            .build();

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .hash(CONTAINER_1_HASH)
            .internalName(CONTAINER_1_INTERNALNAME)
            .containerCreated(CONTAINER_1_CREATED)
            .lastModified(CONTAINER_1_UPDATED)
            .image(IMAGE_1)
            .build();

    public final static Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .hash(CONTAINER_2_HASH)
            .internalName(CONTAINER_2_INTERNALNAME)
            .containerCreated(CONTAINER_2_CREATED)
            .lastModified(CONTAINER_2_UPDATED)
            .image(IMAGE_2)
            .build();

    public final static Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .name(DATABASE_1_NAME)
            .internalName(DATABASE_1_INTERNALNAME)
            .isPublic(DATABASE_1_PUBLIC)
            .container(CONTAINER_1)
            .created(DATABASE_1_CREATED)
            .lastModified(DATABASE_1_UPDATED)
            .container(CONTAINER_1)
            .exchange(DATABASE_1_EXCHANGE)
            .build();

    public final static Database DATABASE_2 = Database.builder()
            .id(DATABASE_2_ID)
            .name(DATABASE_2_NAME)
            .internalName(DATABASE_2_INTERNALNAME)
            .isPublic(DATABASE_2_PUBLIC)
            .container(CONTAINER_2)
            .created(DATABASE_2_CREATED)
            .lastModified(DATABASE_2_UPDATED)
            .container(CONTAINER_2)
            .exchange(DATABASE_2_EXCHANGE)
            .build();

    public final static List<String> IMAGE_1_ENV = List.of("POSTGRES_USER=postgres",
            "POSTGRES_PASSWORD=postgres", "POSTGRES_DB=" + DATABASE_1_INTERNALNAME);

    public final static List<String> IMAGE_2_ENV = List.of("MARIADB_ROOT_PASSWORD=mariadb");
}
