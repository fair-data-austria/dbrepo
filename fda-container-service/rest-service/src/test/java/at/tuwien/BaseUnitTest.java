package at.tuwien;

import at.tuwien.api.container.image.ImageEnvItemDto;
import at.tuwien.api.container.image.ImageEnvItemTypeDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "mariadb";
    public final static String IMAGE_1_TAG = "10.5";
    public final static String IMAGE_1_HASH = "83b40f2726e5";
    public final static Integer IMAGE_1_PORT = 5432;
    public final static String IMAGE_1_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    public final static String IMAGE_1_DRIVER = "org.mariadb.jdbc.Driver";
    public final static String IMAGE_1_JDBC = "mariadb";
    public final static Long IMAGE_1_SIZE = 12000L;
    public final static String IMAGE_1_LOGO = "AAAA";
    public final static Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);
    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_1_ID)
                    .key("MARIADB_USER")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_1_ID)
                    .key("MARIADB_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_1_ID)
                    .key("MARIADB_ROOT_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PRIVILEGED_PASSWORD)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_1_ID)
                    .key("UZERNAME")
                    .value("root")
                    .type(ContainerImageEnvironmentItemType.PRIVILEGED_USERNAME)
                    .build());

    public final static ImageEnvItemDto[] IMAGE_1_ENV_DTO = new ImageEnvItemDto[]{
            ImageEnvItemDto.builder()
                    .iid(IMAGE_1_ID)
                    .key("MARIADB_USER")
                    .value("mariadb")
                    .type(ImageEnvItemTypeDto.USERNAME)
                    .build(),
            ImageEnvItemDto.builder()
                    .iid(IMAGE_1_ID)
                    .key("MARIADB_PASSWORD")
                    .value("mariadb")
                    .type(ImageEnvItemTypeDto.PASSWORD)
                    .build()};

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

    public final static Long IMAGE_2_ID = 2L;
    public final static String IMAGE_2_REPOSITORY = "mysql";
    public final static String IMAGE_2_TAG = "8.0";
    public final static String IMAGE_2_HASH = "83b40f2726e5";
    public final static Integer IMAGE_2_PORT = 3306;
    public final static String IMAGE_2_DIALECT = "org.hibernate.dialect.MysqlDBDialect";
    public final static String IMAGE_2_DRIVER = "org.mysql.jdbc.Driver";
    public final static String IMAGE_2_JDBC = "mysql";
    public final static Long IMAGE_2_SIZE = 12000L;
    public final static String IMAGE_2_LOGO = "BBBB";
    public final static Instant IMAGE_2_BUILT = Instant.now().minus(38, HOURS);
    public final static List<ContainerImageEnvironmentItem> IMAGE_2_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_2_ID)
                    .key("MYSQL_USER")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_2_ID)
                    .key("MYSQL_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_2_ID)
                    .key("MYSQL_ROOT_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PRIVILEGED_PASSWORD)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_2_ID)
                    .key("UZERNAME")
                    .value("root")
                    .type(ContainerImageEnvironmentItemType.PRIVILEGED_USERNAME)
                    .build());
    public final static ContainerImageEnvironmentItem[] IMAGE_2_ENV_ARR = IMAGE_2_ENV.toArray(new ContainerImageEnvironmentItem[0]);

    public final static ContainerImage IMAGE_2 = ContainerImage.builder()
            .id(IMAGE_2_ID)
            .repository(IMAGE_2_REPOSITORY)
            .tag(IMAGE_2_TAG)
            .hash(IMAGE_2_HASH)
            .jdbcMethod(IMAGE_2_JDBC)
            .dialect(IMAGE_2_DIALECT)
            .driverClass(IMAGE_2_DRIVER)
            .containers(List.of())
            .compiled(IMAGE_2_BUILT)
            .size(IMAGE_2_SIZE)
            .environment(IMAGE_2_ENV)
            .defaultPort(IMAGE_2_PORT)
            .logo(IMAGE_2_LOGO)
            .build();

    public final static Long CONTAINER_1_ID = 1L;
    public final static String CONTAINER_1_HASH = "deadbeef";
    public final static String CONTAINER_1_NAME = "fda-userdb-u01";
    public final static String CONTAINER_1_INTERNALNAME = "fda-userdb-u01";
    public final static String CONTAINER_1_DATABASE = "univie";
    public final static String CONTAINER_1_IP = "172.28.0.5";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final static Long CONTAINER_2_ID = 2L;
    public final static String CONTAINER_2_HASH = "deadbeef";
    public final static String CONTAINER_2_NAME = "fda-userdb-u02";
    public final static String CONTAINER_2_INTERNALNAME = "fda-userdb-u02";
    public final static String CONTAINER_2_DATABASE = "univie";
    public final static String CONTAINER_2_IP = "172.28.0.6";
    public final static Instant CONTAINER_2_CREATED = Instant.now().minus(2, HOURS);

    public final static Long CONTAINER_3_ID = 3L;
    public final static String CONTAINER_3_HASH = "deadbeef";
    public final static String CONTAINER_3_NAME = "fda-userdb-u03";
    public final static String CONTAINER_3_INTERNALNAME = "fda-userdb-u03";
    public final static String CONTAINER_3_DATABASE = "u03";
    public final static String CONTAINER_3_IP = "173.38.0.7";
    public final static Instant CONTAINER_3_CREATED = Instant.now().minus(2, HOURS);

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .image(IMAGE_1)
            .hash(CONTAINER_1_HASH)
            .created(CONTAINER_1_CREATED)
            .build();

    public final static Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .internalName(CONTAINER_2_INTERNALNAME)
            .image(IMAGE_1)
            .hash(CONTAINER_2_HASH)
            .created(CONTAINER_2_CREATED)
            .build();

    public final static Container CONTAINER_3 = Container.builder()
            .id(CONTAINER_3_ID)
            .name(CONTAINER_3_NAME)
            .internalName(CONTAINER_3_INTERNALNAME)
            .image(IMAGE_1)
            .hash(CONTAINER_3_HASH)
            .created(CONTAINER_3_CREATED)
            .build();

}
