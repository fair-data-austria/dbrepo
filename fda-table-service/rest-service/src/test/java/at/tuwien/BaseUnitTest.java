package at.tuwien;

import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;

import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "NYSE";
    public final static String DATABASE_1_INTERNALNAME = "nyse";
    public final static String TABLE_1_DESCRIPTION = "New York Stock Exchange";

    public final static Long TABLE_1_ID = 1L;
    public final static String TABLE_1_NAME = "Stock Exchange";
    public final static String TABLE_1_INTERNALNAME = "stock_exchange";

    public final static Long COLUMN_1_ID = 1L;
    public final static Boolean COLUMN_1_PRIMARY = true;
    public final static String COLUMN_1_NAME = "Min";
    public final static ColumnTypeDto COLUMN_1_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_1_NULL = false;
    public final static Long COLUMN_2_ID = 2L;
    public final static Boolean COLUMN_2_PRIMARY = false;
    public final static String COLUMN_2_NAME = "Max";
    public final static ColumnTypeDto COLUMN_2_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_2_NULL = true;
    public final static Long COLUMN_3_ID = 3L;
    public final static Boolean COLUMN_3_PRIMARY = false;
    public final static String COLUMN_3_NAME = "Buy";
    public final static ColumnTypeDto COLUMN_3_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_3_NULL = true;
    public final static Long COLUMN_4_ID = 4L;
    public final static Boolean COLUMN_4_PRIMARY = false;
    public final static String COLUMN_4_NAME = "Sell";
    public final static ColumnTypeDto COLUMN_4_TYPE = ColumnTypeDto.NUMBER;
    public final static Boolean COLUMN_4_NULL = true;
    public final static Long COLUMN_5_ID = 5L;
    public final static Boolean COLUMN_5_PRIMARY = false;
    public final static String COLUMN_5_NAME = "Description";
    public final static ColumnTypeDto COLUMN_5_TYPE = ColumnTypeDto.TEXT;
    public final static Boolean COLUMN_5_NULL = true;

    public final static Long IMAGE_1_ID = 1L;
    public final static String IMAGE_1_REPOSITORY = "postgres";
    public final static String IMAGE_1_TAG = "13-alpine";
    public final static String IMAGE_1_HASH = "83b40f2726e5";
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

    public final static ContainerImage IMAGE_1 = ContainerImage.builder()
            .id(IMAGE_1_ID)
            .repository(IMAGE_1_REPOSITORY)
            .tag(IMAGE_1_TAG)
            .hash(IMAGE_1_HASH)
            .compiled(IMAGE_1_BUILT)
            .size(IMAGE_1_SIZE)
            .environment(IMAGE_1_ENV)
            .defaultPort(IMAGE_1_PORT)
            .build();

    public final static Long CONTAINER_1_ID = 1L;
    public final static String CONTAINER_1_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final static String CONTAINER_1_NAME = "u01";
    public final static String CONTAINER_1_INTERNALNAME = "localhost";
    public final static String CONTAINER_1_DATABASE = "univie";
    public final static String CONTAINER_1_IP = "231.145.98.83";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_1_HASH)
            .containerCreated(CONTAINER_1_CREATED)
            .build();

    public final static Table TABLE_1 = Table.builder()
            .id(TABLE_1_ID)
            .created(Instant.now())
            .internalName(TABLE_1_INTERNALNAME)
            .name(TABLE_1_NAME)
            .lastModified(Instant.now())
            .tdbid(DATABASE_1_ID)
            .build();

    public final static Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .created(Instant.now().minus(1, HOURS))
            .lastModified(Instant.now())
            .isPublic(false)
            .name(DATABASE_1_NAME)
            .container(CONTAINER_1)
            .internalName(DATABASE_1_INTERNALNAME)
            .build();

    public final static ColumnCreateDto[] COLUMNS5 = new ColumnCreateDto[]{
            ColumnCreateDto.builder()
                    .type(COLUMN_1_TYPE)
                    .name(COLUMN_1_NAME)
                    .nullAllowed(COLUMN_1_NULL)
                    .primaryKey(COLUMN_1_PRIMARY)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_2_TYPE)
                    .name(COLUMN_2_NAME)
                    .nullAllowed(COLUMN_2_NULL)
                    .primaryKey(COLUMN_2_PRIMARY)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_3_TYPE)
                    .name(COLUMN_3_NAME)
                    .nullAllowed(COLUMN_3_NULL)
                    .primaryKey(COLUMN_3_PRIMARY)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_4_TYPE)
                    .name(COLUMN_4_NAME)
                    .nullAllowed(COLUMN_4_NULL)
                    .primaryKey(COLUMN_4_PRIMARY)
                    .build(),
            ColumnCreateDto.builder()
                    .type(COLUMN_5_TYPE)
                    .name(COLUMN_5_NAME)
                    .nullAllowed(COLUMN_5_NULL)
                    .primaryKey(COLUMN_5_PRIMARY)
                    .build()};

}
