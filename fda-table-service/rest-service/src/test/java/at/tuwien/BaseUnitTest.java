package at.tuwien;

import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.database.table.columns.TableColumn;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "Weather";
    public final static String DATABASE_1_INTERNALNAME = "weather";
    public final static String TABLE_1_DESCRIPTION = "Weather in the world";

    public final static Long DATABASE_2_ID = 2L;
    public final static String DATABASE_2_NAME = "RIVER";
    public final static String DATABASE_2_INTERNALNAME = "river";
    public final static String TABLE_2_DESCRIPTION = "River Data";

    public final static Long TABLE_1_ID = 1L;
    public final static String TABLE_1_NAME = "Weather AUS";
    public final static String TABLE_1_INTERNALNAME = "weather_aus";

    public final static Long COLUMN_1_ID = 1L;
    public final static Boolean COLUMN_1_PRIMARY = true;
    public final static String COLUMN_1_NAME = "Date";
    public final static String COLUMN_1_INTERNAL_NAME = "mdb_date";
    public final static ColumnTypeDto COLUMN_1_TYPE = ColumnTypeDto.DATE;
    public final static Boolean COLUMN_1_NULL = false;
    public final static Boolean COLUMN_1_UNIQUE = true;

    public final static Long COLUMN_2_ID = 2L;
    public final static Boolean COLUMN_2_PRIMARY = false;
    public final static String COLUMN_2_NAME = "Location";
    public final static String COLUMN_2_INTERNAL_NAME = "mdb_location";
    public final static ColumnTypeDto COLUMN_2_TYPE = ColumnTypeDto.STRING;
    public final static Boolean COLUMN_2_NULL = false;
    public final static Boolean COLUMN_2_UNIQUE = false;

    public final static Long COLUMN_3_ID = 3L;
    public final static Boolean COLUMN_3_PRIMARY = false;
    public final static String COLUMN_3_NAME = "MinTemp";
    public final static String COLUMN_3_INTERNAL_NAME = "mdb_min_temp";
    public final static ColumnTypeDto COLUMN_3_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_3_NULL = false;
    public final static Boolean COLUMN_3_UNIQUE = false;

    public final static Long COLUMN_4_ID = 4L;
    public final static Boolean COLUMN_4_PRIMARY = false;
    public final static String COLUMN_4_NAME = "MaxTemp";
    public final static String COLUMN_4_INTERNAL_NAME = "mdb_max_temp";
    public final static ColumnTypeDto COLUMN_4_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_4_NULL = false;
    public final static Boolean COLUMN_4_UNIQUE = false;

    public final static Long COLUMN_5_ID = 5L;
    public final static Boolean COLUMN_5_PRIMARY = false;
    public final static String COLUMN_5_NAME = "Rainfall";
    public final static String COLUMN_5_INTERNAL_NAME = "mdb_rainfall";
    public final static ColumnTypeDto COLUMN_5_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_5_NULL = false;
    public final static Boolean COLUMN_5_UNIQUE = false;

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "postgres";
    public final static String IMAGE_1_TAG = "13-alpine";
    public final static String IMAGE_1_HASH = "83b40f2726e5";
    public final static String IMAGE_1_DIALECT = "org.hibernate.dialect.PostgreSQLDialect";
    public final static String IMAGE_1_DRIVER = "org.postgresql.Driver";
    public final static String IMAGE_1_JDBC = "postgresql";
    public final static Integer IMAGE_1_PORT = 5433;
    public final static Long IMAGE_1_SIZE = 12000L;
    public final static Instant IMAGE_1_BUILT = Instant.now().minus(40, HOURS);

    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .key("POSTGRES_PASSWORD")
                    .value("postgres")
                    .build());

    public final static List<String> IMAGE_1_ENVIRONMENT = List.of("POSTGRES_USER=postgres",
            "POSTGRES_PASSWORD=postgres", "POSTGRES_DB=" + DATABASE_1_INTERNALNAME);

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
            .build();

    public final static Long CONTAINER_1_ID = 1L;
    public static String CONTAINER_1_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final static String CONTAINER_1_NAME = "u01";
    public final static String CONTAINER_1_INTERNALNAME = "localhost";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final static Long CONTAINER_2_ID = 2L;
    public final static String CONTAINER_2_HASH = "deadbeef";
    public final static String CONTAINER_2_NAME = "u02";
    public final static String CONTAINER_2_INTERNALNAME = "not3x1st1ng";
    public final static Instant CONTAINER_2_CREATED = Instant.now().minus(1, HOURS);

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
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_2_HASH)
            .containerCreated(CONTAINER_2_CREATED)
            .build();

    public final static List<TableColumn> TABLE_1_COLUMNS = List.of(TableColumn.builder()
                    .id(COLUMN_1_ID)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_1_NAME)
                    .internalName(COLUMN_1_INTERNAL_NAME)
                    .columnType(COLUMN_1_TYPE.name())
                    .isNullAllowed(COLUMN_1_NULL)
                    .isPrimaryKey(COLUMN_1_PRIMARY)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_2_ID)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_2_NAME)
                    .internalName(COLUMN_2_INTERNAL_NAME)
                    .columnType(COLUMN_2_TYPE.name())
                    .isNullAllowed(COLUMN_2_NULL)
                    .isPrimaryKey(COLUMN_2_PRIMARY)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_3_ID)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_3_NAME)
                    .internalName(COLUMN_3_INTERNAL_NAME)
                    .columnType(COLUMN_3_TYPE.name())
                    .isNullAllowed(COLUMN_3_NULL)
                    .isPrimaryKey(COLUMN_3_PRIMARY)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_4_ID)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_4_NAME)
                    .internalName(COLUMN_4_INTERNAL_NAME)
                    .columnType(COLUMN_4_TYPE.name())
                    .isNullAllowed(COLUMN_4_NULL)
                    .isPrimaryKey(COLUMN_4_PRIMARY)
                    .build(),
            TableColumn.builder()
                    .id(COLUMN_5_ID)
                    .cdbid(DATABASE_1_ID)
                    .tid(TABLE_1_ID)
                    .name(COLUMN_5_NAME)
                    .internalName(COLUMN_5_INTERNAL_NAME)
                    .columnType(COLUMN_5_TYPE.name())
                    .isNullAllowed(COLUMN_5_NULL)
                    .isPrimaryKey(COLUMN_5_PRIMARY)
                    .build());

    public final static Table TABLE_1 = Table.builder()
            .id(TABLE_1_ID)
            .created(Instant.now())
            .internalName(TABLE_1_INTERNALNAME)
            .name(TABLE_1_NAME)
            .lastModified(Instant.now())
            .columns(TABLE_1_COLUMNS)
            .tdbid(DATABASE_1_ID)
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
            .build();

    /* no connection */
    public final static Database DATABASE_2 = Database.builder()
            .id(DATABASE_2_ID)
            .created(Instant.now().minus(1, HOURS))
            .lastModified(Instant.now())
            .isPublic(false)
            .name(DATABASE_2_NAME)
            .tables(List.of())
            .container(CONTAINER_2)
            .internalName(DATABASE_2_INTERNALNAME)
            .build();

    public final static ColumnCreateDto[] COLUMNS5 = new ColumnCreateDto[]{
            ColumnCreateDto.builder()
                    .type(COLUMN_1_TYPE)
                    .name(COLUMN_1_NAME)
                    .nullAllowed(COLUMN_1_NULL)
                    .primaryKey(COLUMN_1_PRIMARY)
                    .unique(COLUMN_1_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_2_TYPE)
                    .name(COLUMN_2_NAME)
                    .nullAllowed(COLUMN_2_NULL)
                    .primaryKey(COLUMN_2_PRIMARY)
                    .unique(COLUMN_2_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_3_TYPE)
                    .name(COLUMN_3_NAME)
                    .nullAllowed(COLUMN_3_NULL)
                    .primaryKey(COLUMN_3_PRIMARY)
                    .unique(COLUMN_3_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_4_TYPE)
                    .name(COLUMN_4_NAME)
                    .nullAllowed(COLUMN_4_NULL)
                    .primaryKey(COLUMN_4_PRIMARY)
                    .unique(COLUMN_4_UNIQUE)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_5_TYPE)
                    .name(COLUMN_5_NAME)
                    .nullAllowed(COLUMN_5_NULL)
                    .primaryKey(COLUMN_5_PRIMARY)
                    .unique(COLUMN_5_UNIQUE)
                    .build()};

    public final static ColumnCreateDto[] COLUMNS4 = new ColumnCreateDto[]{
            ColumnCreateDto.builder()
                    .type(COLUMN_1_TYPE)
                    .name(COLUMN_1_NAME)
                    .nullAllowed(COLUMN_1_NULL)
                    .primaryKey(COLUMN_1_PRIMARY)
                    .build()};

    public final static String COLUMNS5_CREATE = "CREATE TABLE " + TABLE_1_INTERNALNAME + "(Min INTEGER NOT NULL PRIMARY KEY,Max INTEGER NULL,Buy INTEGER NULL,Sell INTEGER NULL,Description TEXT NULL);";

}
