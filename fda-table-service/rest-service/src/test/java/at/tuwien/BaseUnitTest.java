package at.tuwien;

import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import at.tuwien.entities.database.table.columns.TableColumnType;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;
import static java.time.temporal.ChronoUnit.SECONDS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest extends CsvUnitTest {

    public final static String DATABASE_NET = "fda-userdb";

    public final static String BROKER_IMAGE = "fda-broker-service:latest";
    public final static String BROKER_INTERNALNAME = "fda-broker-service";
    public final static String BROKER_NET = "fda-public";
    public final static String BROKER_IP = "172.29.0.2";

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "Weather";
    public final static String DATABASE_1_INTERNALNAME = "weather";
    public final static String DATABASE_1_EXCHANGE = "fda." + DATABASE_1_INTERNALNAME;
    public final static Instant DATABASE_1_CREATED = Instant.now().minus(2, SECONDS);

    public final static Long DATABASE_2_ID = 2L;
    public final static String DATABASE_2_NAME = "Weather";
    public final static String DATABASE_2_INTERNALNAME = "weather";
    public final static String DATABASE_2_EXCHANGE = "fda." + DATABASE_2_INTERNALNAME;

    public final static Long DATABASE_3_ID = 3L;
    public final static String DATABASE_3_NAME = "Biomedical";
    public final static String DATABASE_3_INTERNALNAME = "biomedical";
    public final static String DATABASE_3_EXCHANGE = "fda." + DATABASE_3_INTERNALNAME;

    public final static Long TABLE_1_ID = 1L;
    public final static String TABLE_1_NAME = "Weather AUS";
    public final static String TABLE_1_INTERNALNAME = "weather_aus";
    public final static String TABLE_1_DESCRIPTION = "Weather in the world";
    public final static String TABLE_1_TOPIC = DATABASE_1_EXCHANGE + "." + TABLE_1_INTERNALNAME;
    public final static Instant TABLE_1_LAST_MODIFIED = Instant.now();
    public final static Boolean TABLE_1_SKIP_HEADERS = true;
    public final static String TABLE_1_NULL_ELEMENT = "NA";
    public final static Character TABLE_1_SEPARATOR = ',';
    public final static String TABLE_1_TRUE_ELEMENT = null;
    public final static String TABLE_1_FALSE_ELEMENT = null;

    public final static Long TABLE_2_ID = 2L;
    public final static String TABLE_2_NAME = "Weather AT";
    public final static String TABLE_2_INTERNALNAME = "weather_at";
    public final static String TABLE_2_DESCRIPTION = "Weather in austria";
    public final static String TABLE_2_TOPIC = DATABASE_2_EXCHANGE + "." + TABLE_2_INTERNALNAME;
    public final static Instant TABLE_2_LAST_MODIFIED = Instant.now();
    public final static Boolean TABLE_2_SKIP_HEADERS = true;
    public final static String TABLE_2_NULL_ELEMENT = null;
    public final static Character TABLE_2_SEPARATOR = ';';
    public final static String TABLE_2_TRUE_ELEMENT = null;
    public final static String TABLE_2_FALSE_ELEMENT = null;

    public final static Long TABLE_3_ID = 3L;
    public final static String TABLE_3_NAME = "MALDI MS Data";
    public final static String TABLE_3_INTERNALNAME = "maldi_ms_data";
    public final static String TABLE_3_DESCRIPTION = "See Ruffini-Ronzani et al. (https://doi.org/10.1098/rsos.210210)";
    public final static String TABLE_3_TOPIC = DATABASE_3_EXCHANGE + "." + TABLE_3_INTERNALNAME;
    public final static Instant TABLE_3_LAST_MODIFIED = Instant.now();
    public final static Boolean TABLE_3_SKIP_HEADERS = false;
    public final static String TABLE_3_NULL_ELEMENT = null;
    public final static Character TABLE_3_SEPARATOR = ',';
    public final static String TABLE_3_TRUE_ELEMENT = null;
    public final static String TABLE_3_FALSE_ELEMENT = null;

    public final static Long COLUMN_1_1_ID = 1L;
    public final static Integer COLUMN_1_1_ORDINALPOS = 0;
    public final static Boolean COLUMN_1_1_PRIMARY = true;
    public final static String COLUMN_1_1_NAME = "id";
    public final static String COLUMN_1_1_INTERNAL_NAME = "id";
    public final static TableColumnType COLUMN_1_1_TYPE = TableColumnType.NUMBER;
    public final static ColumnTypeDto COLUMN_1_1_TYPE_DTO = ColumnTypeDto.NUMBER;
    public final static String COLUMN_1_1_DATE_FORMAT = null;
    public final static Boolean COLUMN_1_1_NULL = false;
    public final static Boolean COLUMN_1_1_UNIQUE = true;
    public final static Boolean COLUMN_1_1_AUTO_GENERATED = false;
    public final static String COLUMN_1_1_FOREIGN_KEY = null;
    public final static String COLUMN_1_1_CHECK = null;
    public final static List<String> COLUMN_1_1_ENUM_VALUES = null;

    public final static Long COLUMN_1_2_ID = 2L;
    public final static Integer COLUMN_1_2_ORDINALPOS = 1;
    public final static Boolean COLUMN_1_2_PRIMARY = false;
    public final static String COLUMN_1_2_NAME = "Date";
    public final static String COLUMN_1_2_INTERNAL_NAME = "date";
    public final static TableColumnType COLUMN_1_2_TYPE = TableColumnType.DATE;
    public final static ColumnTypeDto COLUMN_1_2_TYPE_DTO = ColumnTypeDto.DATE;
    public final static String COLUMN_1_2_DATE_FORMAT = "yyyy-MM-dd";
    public final static Boolean COLUMN_1_2_NULL = true;
    public final static Boolean COLUMN_1_2_UNIQUE = false;
    public final static Boolean COLUMN_1_2_AUTO_GENERATED = false;
    public final static String COLUMN_1_2_FOREIGN_KEY = null;
    public final static String COLUMN_1_2_CHECK = null;
    public final static List<String> COLUMN_1_2_ENUM_VALUES = null;

    public final static Long COLUMN_1_3_ID = 3L;
    public final static Integer COLUMN_1_3_ORDINALPOS = 2;
    public final static Boolean COLUMN_1_3_PRIMARY = false;
    public final static String COLUMN_1_3_NAME = "Location";
    public final static String COLUMN_1_3_INTERNAL_NAME = "location";
    public final static TableColumnType COLUMN_1_3_TYPE = TableColumnType.STRING;
    public final static ColumnTypeDto COLUMN_1_3_TYPE_DTO = ColumnTypeDto.STRING;
    public final static String COLUMN_1_3_DATE_FORMAT = null;
    public final static Boolean COLUMN_1_3_NULL = true;
    public final static Boolean COLUMN_1_3_UNIQUE = false;
    public final static Boolean COLUMN_1_3_AUTO_GENERATED = false;
    public final static String COLUMN_1_3_FOREIGN_KEY = null;
    public final static String COLUMN_1_3_CHECK = null;
    public final static List<String> COLUMN_1_3_ENUM_VALUES = null;

    public final static Long COLUMN_1_4_ID = 4L;
    public final static Integer COLUMN_1_4_ORDINALPOS = 3;
    public final static Boolean COLUMN_1_4_PRIMARY = false;
    public final static String COLUMN_1_4_NAME = "MinTemp";
    public final static String COLUMN_1_4_INTERNAL_NAME = "mintemp";
    public final static TableColumnType COLUMN_1_4_TYPE = TableColumnType.DECIMAL;
    public final static ColumnTypeDto COLUMN_1_4_TYPE_DTO = ColumnTypeDto.DECIMAL;
    public final static String COLUMN_1_4_DATE_FORMAT = null;
    public final static Boolean COLUMN_1_4_NULL = true;
    public final static Boolean COLUMN_1_4_UNIQUE = false;
    public final static Boolean COLUMN_1_4_AUTO_GENERATED = false;
    public final static String COLUMN_1_4_FOREIGN_KEY = null;
    public final static String COLUMN_1_4_CHECK = null;
    public final static List<String> COLUMN_1_4_ENUM_VALUES = null;

    public final static Long COLUMN_1_5_ID = 5L;
    public final static Integer COLUMN_1_5_ORDINALPOS = 4;
    public final static Boolean COLUMN_1_5_PRIMARY = false;
    public final static String COLUMN_1_5_NAME = "Rainfall";
    public final static String COLUMN_1_5_INTERNAL_NAME = "rainfall";
    public final static TableColumnType COLUMN_1_5_TYPE = TableColumnType.DECIMAL;
    public final static ColumnTypeDto COLUMN_1_5_TYPE_DTO = ColumnTypeDto.DECIMAL;
    public final static String COLUMN_1_5_DATE_FORMAT = null;
    public final static Boolean COLUMN_1_5_NULL = true;
    public final static Boolean COLUMN_1_5_UNIQUE = false;
    public final static Boolean COLUMN_1_5_AUTO_GENERATED = false;
    public final static String COLUMN_1_5_FOREIGN_KEY = null;
    public final static String COLUMN_1_5_CHECK = null;
    public final static List<String> COLUMN_1_5_ENUM_VALUES = null;

    public final static Long COLUMN_3_1_ID = 1L;
    public final static Integer COLUMN_3_1_ORDINALPOS = 0;
    public final static Boolean COLUMN_3_1_PRIMARY = false;
    public final static String COLUMN_3_1_NAME = "qu";
    public final static String COLUMN_3_1_INTERNAL_NAME = "qu";
    public final static TableColumnType COLUMN_3_1_TYPE = TableColumnType.STRING;
    public final static ColumnTypeDto COLUMN_3_1_TYPE_DTO = ColumnTypeDto.STRING;
    public final static Boolean COLUMN_3_1_NULL = false;
    public final static Boolean COLUMN_3_1_UNIQUE = false;
    public final static Boolean COLUMN_3_1_AUTO_GENERATED = false;
    public final static String COLUMN_3_1_FOREIGN_KEY = null;
    public final static String COLUMN_3_1_CHECK = null;
    public final static List<String> COLUMN_3_1_ENUM_VALUES = null;

    public final static Long COLUMN_3_2_ID = 2L;
    public final static Integer COLUMN_3_2_ORDINALPOS = 1;
    public final static Boolean COLUMN_3_2_PRIMARY = false;
    public final static String COLUMN_3_2_NAME = "species";
    public final static String COLUMN_3_2_INTERNAL_NAME = "species";
    public final static TableColumnType COLUMN_3_2_TYPE = TableColumnType.ENUM;
    public final static ColumnTypeDto COLUMN_3_2_TYPE_DTO = ColumnTypeDto.ENUM;
    public final static Boolean COLUMN_3_2_NULL = false;
    public final static Boolean COLUMN_3_2_UNIQUE = false;
    public final static Boolean COLUMN_3_2_AUTO_GENERATED = false;
    public final static String COLUMN_3_2_FOREIGN_KEY = null;
    public final static String COLUMN_3_2_CHECK = null;
    public final static List<String> COLUMN_3_2_ENUM_VALUES = List.of("sheep", "calf", "undetermined", "sheep or goat", "goat", "not anal.");

    public final static Long COLUMN_3_3_ID = 3L;
    public final static Integer COLUMN_3_3_ORDINALPOS = 2;
    public final static Boolean COLUMN_3_3_PRIMARY = false;
    public final static String COLUMN_3_3_NAME = "score";
    public final static String COLUMN_3_3_INTERNAL_NAME = "score";
    public final static TableColumnType COLUMN_3_3_TYPE = TableColumnType.STRING;
    public final static ColumnTypeDto COLUMN_3_3_TYPE_DTO = ColumnTypeDto.STRING;
    public final static Boolean COLUMN_3_3_NULL = false;
    public final static Boolean COLUMN_3_3_UNIQUE = false;
    public final static Boolean COLUMN_3_3_AUTO_GENERATED = false;
    public final static String COLUMN_3_3_FOREIGN_KEY = null;
    public final static String COLUMN_3_3_CHECK = null;
    public final static List<String> COLUMN_3_3_ENUM_VALUES = null;

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "mariadb";
    public final static String IMAGE_1_TAG = "10.5";
    public final static String IMAGE_1_HASH = "d6a5e003eae42397f7ee4589e9f21e231d3721ac131970d2286bd616e7f55bb4\n";
    public final static String IMAGE_1_DIALECT = "org.hibernate.dialect.MariaDBDialect";
    public final static String IMAGE_1_DRIVER = "org.mariadb.jdbc.Driver";
    public final static String IMAGE_1_JDBC = "mariadb";
    public final static String IMAGE_1_LOGO = "AAAA";
    public final static Integer IMAGE_1_PORT = 3306;
    public final static Long IMAGE_1_SIZE = 12000L;
    public final static Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);

    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("UZERNAME")
                    .value("root")
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("MARIADB_USER")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.OTHER)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("MARIADB_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.OTHER)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("MARIADB_ROOT_PASSWORD")
                    .value("mariadb")
                    .type(ContainerImageEnvironmentItemType.PASSWORD)
                    .build());

    public final static ContainerImage IMAGE_1 = ContainerImage.builder()
            .id(IMAGE_1_ID)
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .compiled(IMAGE_1_BUILT)
            .dialect(IMAGE_1_DIALECT)
            .jdbcMethod(IMAGE_1_JDBC)
            .driverClass(IMAGE_1_DRIVER)
            .size(IMAGE_1_SIZE)
            .environment(IMAGE_1_ENV)
            .defaultPort(IMAGE_1_PORT)
            .logo(IMAGE_1_LOGO)
            .build();

    public final static Long CONTAINER_1_ID = 1L;
    public final static String CONTAINER_1_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final static String CONTAINER_1_NAME = "u01";
    public final static String CONTAINER_1_INTERNALNAME = "fda-userdb-u01";
    public final static String CONTAINER_1_IP = "172.28.0.5";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final static Long CONTAINER_2_ID = 2L;
    public final static String CONTAINER_2_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_2_IMAGE = IMAGE_1;
    public final static String CONTAINER_2_NAME = "u02";
    public final static String CONTAINER_2_INTERNALNAME = "fda-userdb-u02";
    public final static String CONTAINER_2_IP = "172.28.0.6";
    public final static Instant CONTAINER_2_CREATED = Instant.now().minus(1, HOURS);

    public final static Long CONTAINER_3_ID = 3L;
    public final static String CONTAINER_3_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_3_IMAGE = IMAGE_1;
    public final static String CONTAINER_3_NAME = "u03";
    public final static String CONTAINER_3_INTERNALNAME = "fda-userdb-u03";
    public final static String CONTAINER_3_IP = "172.28.0.7";
    public final static Instant CONTAINER_3_CREATED = Instant.now().minus(1, HOURS);

    public final static Long CONTAINER_NGINX_ID = 4L;
    public final static String CONTAINER_NGINX_HASH = "deadbeef";
    public final static String CONTAINER_NGINX_IMAGE = "nginx";
    public final static String CONTAINER_NGINX_TAG = "1.20-alpine";
    public final static String CONTAINER_NGINX_NET = "fda-public";
    public final static String CONTAINER_NGINX_NAME = "file-service";
    public final static String CONTAINER_NGINX_INTERNALNAME = "fda-test-file-service";
    public final static String CONTAINER_NGINX_IP = "172.29.0.3";
    public final static Instant CONTAINER_NGINX_CREATED = Instant.now().minus(3, HOURS);

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_1_HASH)
            .containerCreated(CONTAINER_1_CREATED)
            .build();

    public final static Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .internalName(CONTAINER_2_INTERNALNAME)
            .image(CONTAINER_2_IMAGE)
            .hash(CONTAINER_2_HASH)
            .containerCreated(CONTAINER_2_CREATED)
            .build();

    public final static Container CONTAINER_3 = Container.builder()
            .id(CONTAINER_3_ID)
            .name(CONTAINER_3_NAME)
            .internalName(CONTAINER_3_INTERNALNAME)
            .image(CONTAINER_3_IMAGE)
            .hash(CONTAINER_3_HASH)
            .containerCreated(CONTAINER_3_CREATED)
            .build();

    public final static Container CONTAINER_NGINX = Container.builder()
            .id(CONTAINER_NGINX_ID)
            .name(CONTAINER_NGINX_NAME)
            .internalName(CONTAINER_NGINX_INTERNALNAME)
            .hash(CONTAINER_NGINX_HASH)
            .containerCreated(CONTAINER_NGINX_CREATED)
            .build();

    public final static List<TableColumn> TABLE_3_COLUMNS = List.of(TableColumn.builder()
                    .id(COLUMN_3_1_ID)
                    .ordinalPosition(COLUMN_3_1_ORDINALPOS)
                    .cdbid(DATABASE_3_ID)
                    .tid(TABLE_3_ID)
                    .name(COLUMN_3_1_NAME)
                    .internalName(COLUMN_3_1_INTERNAL_NAME)
                    .columnType(COLUMN_3_1_TYPE)
                    .isNullAllowed(COLUMN_3_1_NULL)
                    .isUnique(COLUMN_3_1_UNIQUE)
                    .autoGenerated(COLUMN_3_1_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_3_1_PRIMARY)
                    .enumValues(COLUMN_3_1_ENUM_VALUES)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_3_2_ID)
                    .ordinalPosition(COLUMN_3_2_ORDINALPOS)
                    .cdbid(DATABASE_3_ID)
                    .tid(TABLE_3_ID)
                    .name(COLUMN_3_2_NAME)
                    .internalName(COLUMN_3_2_INTERNAL_NAME)
                    .columnType(COLUMN_3_2_TYPE)
                    .isNullAllowed(COLUMN_3_2_NULL)
                    .isUnique(COLUMN_3_2_UNIQUE)
                    .autoGenerated(COLUMN_3_2_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_3_2_PRIMARY)
                    .enumValues(COLUMN_3_2_ENUM_VALUES)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_3_3_ID)
                    .ordinalPosition(COLUMN_3_3_ORDINALPOS)
                    .cdbid(DATABASE_3_ID)
                    .tid(TABLE_3_ID)
                    .name(COLUMN_3_3_NAME)
                    .internalName(COLUMN_3_3_INTERNAL_NAME)
                    .columnType(COLUMN_3_3_TYPE)
                    .isNullAllowed(COLUMN_3_3_NULL)
                    .isUnique(COLUMN_3_3_UNIQUE)
                    .autoGenerated(COLUMN_3_3_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_3_3_PRIMARY)
                    .enumValues(COLUMN_3_3_ENUM_VALUES)
                    .build());

    public final static List<TableColumn> TABLE_1_COLUMNS = List.of(TableColumn.builder()
                    .id(COLUMN_1_1_ID)
                    .ordinalPosition(COLUMN_1_1_ORDINALPOS)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_1_1_NAME)
                    .internalName(COLUMN_1_1_INTERNAL_NAME)
                    .columnType(COLUMN_1_1_TYPE)
                    .dateFormat(COLUMN_1_1_DATE_FORMAT)
                    .isNullAllowed(COLUMN_1_1_NULL)
                    .isUnique(COLUMN_1_1_UNIQUE)
                    .autoGenerated(COLUMN_1_1_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_1_1_PRIMARY)
                    .enumValues(COLUMN_1_1_ENUM_VALUES)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_1_2_ID)
                    .ordinalPosition(COLUMN_1_2_ORDINALPOS)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_1_2_NAME)
                    .internalName(COLUMN_1_2_INTERNAL_NAME)
                    .columnType(COLUMN_1_2_TYPE)
                    .dateFormat(COLUMN_1_2_DATE_FORMAT)
                    .isNullAllowed(COLUMN_1_2_NULL)
                    .isUnique(COLUMN_1_2_UNIQUE)
                    .autoGenerated(COLUMN_1_2_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_1_2_PRIMARY)
                    .enumValues(COLUMN_1_2_ENUM_VALUES)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_1_3_ID)
                    .ordinalPosition(COLUMN_1_3_ORDINALPOS)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_1_3_NAME)
                    .internalName(COLUMN_1_3_INTERNAL_NAME)
                    .columnType(COLUMN_1_3_TYPE)
                    .dateFormat(COLUMN_1_3_DATE_FORMAT)
                    .isNullAllowed(COLUMN_1_3_NULL)
                    .isUnique(COLUMN_1_3_UNIQUE)
                    .autoGenerated(COLUMN_1_3_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_1_3_PRIMARY)
                    .enumValues(COLUMN_1_3_ENUM_VALUES)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_1_4_ID)
                    .ordinalPosition(COLUMN_1_4_ORDINALPOS)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_1_4_NAME)
                    .internalName(COLUMN_1_4_INTERNAL_NAME)
                    .columnType(COLUMN_1_4_TYPE)
                    .dateFormat(COLUMN_1_4_DATE_FORMAT)
                    .isNullAllowed(COLUMN_1_4_NULL)
                    .isUnique(COLUMN_1_4_UNIQUE)
                    .autoGenerated(COLUMN_1_4_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_1_4_PRIMARY)
                    .enumValues(COLUMN_1_4_ENUM_VALUES)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_1_5_ID)
                    .ordinalPosition(COLUMN_1_5_ORDINALPOS)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_1_5_NAME)
                    .internalName(COLUMN_1_5_INTERNAL_NAME)
                    .columnType(COLUMN_1_5_TYPE)
                    .dateFormat(COLUMN_1_5_DATE_FORMAT)
                    .isNullAllowed(COLUMN_1_5_NULL)
                    .isUnique(COLUMN_1_5_UNIQUE)
                    .autoGenerated(COLUMN_1_5_AUTO_GENERATED)
                    .isPrimaryKey(COLUMN_1_5_PRIMARY)
                    .enumValues(COLUMN_1_5_ENUM_VALUES)
                    .build());

    public final static Table TABLE_1 = Table.builder()
            .id(TABLE_1_ID)
            .created(Instant.now())
            .internalName(TABLE_1_INTERNALNAME)
            .description(TABLE_1_DESCRIPTION)
            .name(TABLE_1_NAME)
            .lastModified(TABLE_1_LAST_MODIFIED)
            .columns(TABLE_1_COLUMNS)
            .tdbid(DATABASE_1_ID)
            .topic(TABLE_1_TOPIC)
            .separator(TABLE_1_SEPARATOR)
            .nullElement(TABLE_1_NULL_ELEMENT)
            .trueElement(TABLE_1_TRUE_ELEMENT)
            .falseElement(TABLE_1_FALSE_ELEMENT)
            .skipHeaders(TABLE_1_SKIP_HEADERS)
            .build();

    public final static Table TABLE_2 = Table.builder()
            .id(TABLE_2_ID)
            .created(Instant.now())
            .internalName(TABLE_2_INTERNALNAME)
            .description(TABLE_2_DESCRIPTION)
            .name(TABLE_2_NAME)
            .lastModified(TABLE_2_LAST_MODIFIED)
            .tdbid(DATABASE_2_ID)
            .topic(TABLE_2_TOPIC)
            .separator(TABLE_2_SEPARATOR)
            .nullElement(TABLE_2_NULL_ELEMENT)
            .trueElement(TABLE_2_TRUE_ELEMENT)
            .falseElement(TABLE_2_FALSE_ELEMENT)
            .skipHeaders(TABLE_2_SKIP_HEADERS)
            .build();


    public final static Table TABLE_3 = Table.builder()
            .id(TABLE_3_ID)
            .tdbid(DATABASE_3_ID)
            .created(Instant.now())
            .internalName(TABLE_3_INTERNALNAME)
            .description(TABLE_3_DESCRIPTION)
            .name(TABLE_3_NAME)
            .lastModified(TABLE_3_LAST_MODIFIED)
            .columns(TABLE_3_COLUMNS)
            .topic(TABLE_3_TOPIC)
            .separator(TABLE_3_SEPARATOR)
            .nullElement(TABLE_3_NULL_ELEMENT)
            .trueElement(TABLE_3_TRUE_ELEMENT)
            .falseElement(TABLE_3_FALSE_ELEMENT)
            .skipHeaders(TABLE_3_SKIP_HEADERS)
            .build();

    public final static Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .created(Instant.now().minus(1, HOURS))
            .lastModified(Instant.now())
            .isPublic(false)
            .name(DATABASE_1_NAME)
            .container(CONTAINER_1)
            .tables(List.of(TABLE_1))
            .internalName(DATABASE_1_INTERNALNAME)
            .exchange(DATABASE_1_EXCHANGE)
            .build();

    /* no connection */
    public final static Database DATABASE_2 = Database.builder()
            .id(DATABASE_2_ID)
            .created(Instant.now().minus(1, HOURS))
            .lastModified(Instant.now())
            .isPublic(false)
            .name(DATABASE_2_NAME)
            .tables(List.of(TABLE_2))
            .container(CONTAINER_2)
            .internalName(DATABASE_2_INTERNALNAME)
            .exchange(DATABASE_2_EXCHANGE)
            .build();

    public final static Database DATABASE_3 = Database.builder()
            .id(DATABASE_3_ID)
            .created(Instant.now().minus(1, HOURS))
            .lastModified(Instant.now())
            .isPublic(false)
            .name(DATABASE_3_NAME)
            .container(CONTAINER_3)
            .tables(List.of(TABLE_3))
            .internalName(DATABASE_3_INTERNALNAME)
            .exchange(DATABASE_3_EXCHANGE)
            .build();

    public final static ColumnCreateDto[] COLUMNS_CSV01 = new ColumnCreateDto[]{
            ColumnCreateDto.builder()
                    .type(COLUMN_1_1_TYPE_DTO)
                    .name(COLUMN_1_1_NAME)
                    .nullAllowed(COLUMN_1_1_NULL)
                    .primaryKey(COLUMN_1_1_PRIMARY)
                    .unique(COLUMN_1_1_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_1_2_TYPE_DTO)
                    .name(COLUMN_1_2_NAME)
                    .nullAllowed(COLUMN_1_2_NULL)
                    .primaryKey(COLUMN_1_2_PRIMARY)
                    .unique(COLUMN_1_2_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_1_3_TYPE_DTO)
                    .name(COLUMN_1_3_NAME)
                    .nullAllowed(COLUMN_1_3_NULL)
                    .primaryKey(COLUMN_1_3_PRIMARY)
                    .unique(COLUMN_1_3_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_1_4_TYPE_DTO)
                    .name(COLUMN_1_4_NAME)
                    .nullAllowed(COLUMN_1_4_NULL)
                    .primaryKey(COLUMN_1_4_PRIMARY)
                    .unique(COLUMN_1_4_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_1_5_TYPE_DTO)
                    .name(COLUMN_1_5_NAME)
                    .nullAllowed(COLUMN_1_5_NULL)
                    .primaryKey(COLUMN_1_5_PRIMARY)
                    .unique(COLUMN_1_5_UNIQUE)
                    .build()};

    public final static TableCreateDto TABLE_2_CREATE_DTO = TableCreateDto.builder()
            .name(TABLE_2_NAME)
            .description(TABLE_2_DESCRIPTION)
            .columns(COLUMNS_CSV01)
            .build();

}
