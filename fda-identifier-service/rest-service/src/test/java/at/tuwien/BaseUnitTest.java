package at.tuwien;

import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.api.identifier.CreatorDto;
import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.entities.identifier.Creator;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.entities.identifier.VisibilityType;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.test.context.TestPropertySource;

import java.beans.Visibility;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.time.temporal.ChronoUnit.HOURS;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "Test Database";
    public final static String DATABASE_1_INTERNAL_NAME = "test_dataase";
    public final static String DATABASE_1_EXCHANGE = "fda." + DATABASE_1_INTERNAL_NAME;
    public final static Boolean DATABASE_1_PUBLIC = true;

    public final static Long TABLE_1_ID = 1L;
    public final static String TABLE_1_NAME = "Rainfall";
    public final static String TABLE_1_INTERNAL_NAME = "rainfall";
    public final static String TABLE_1_TOPIC = DATABASE_1_EXCHANGE + "." + TABLE_1_INTERNAL_NAME;

    public final static Long CREATOR_1_ID = 1L;
    public final static Long CREATOR_1_QUERY_ID = 1L;
    public final static Long CREATOR_1_IDENTIFIER_ID = 1L;
    public final static String CREATOR_1_FIRSTNAME = "Max";
    public final static String CREATOR_1_LASTNAME = "Mustermann";
    public final static Instant CREATOR_1_CREATED = Instant.now();
    public final static Instant CREATOR_1_MODIFIED = Instant.now().minus(1, HOURS);

    public final static Creator CREATOR_1 = Creator.builder()
            .id(CREATOR_1_ID)
            .pid(CREATOR_1_IDENTIFIER_ID)
            .firstname(CREATOR_1_FIRSTNAME)
            .lastname(CREATOR_1_LASTNAME)
            .created(CREATOR_1_CREATED)
            .lastModified(CREATOR_1_MODIFIED)
            .build();

    public final static CreatorDto CREATOR_1_DTO = CreatorDto.builder()
            .id(CREATOR_1_ID)
            .pid(CREATOR_1_IDENTIFIER_ID)
            .firstname(CREATOR_1_FIRSTNAME)
            .lastname(CREATOR_1_LASTNAME)
            .created(CREATOR_1_CREATED)
            .lastModified(CREATOR_1_MODIFIED)
            .build();

    public final static Creator CREATOR_1_REQUEST = Creator.builder()
            .pid(CREATOR_1_IDENTIFIER_ID)
            .firstname(CREATOR_1_FIRSTNAME)
            .lastname(CREATOR_1_LASTNAME)
            .created(CREATOR_1_CREATED)
            .lastModified(CREATOR_1_MODIFIED)
            .build();

    public final static Long CREATOR_2_ID = 2L;
    public final static Long CREATOR_2_QUERY_ID = 2L;
    public final static Long CREATOR_2_IDENTIFIER_ID = 2L;
    public final static String CREATOR_2_FIRSTNAME = "Martina";
    public final static String CREATOR_2_LASTNAME = "Mustermann";
    public final static Instant CREATOR_2_CREATED = Instant.now();
    public final static Instant CREATOR_2_MODIFIED = Instant.now().minus(1, HOURS);

    public final static Creator CREATOR_2 = Creator.builder()
            .id(CREATOR_2_ID)
            .pid(CREATOR_2_IDENTIFIER_ID)
            .firstname(CREATOR_2_FIRSTNAME)
            .lastname(CREATOR_2_LASTNAME)
            .created(CREATOR_2_CREATED)
            .lastModified(CREATOR_2_MODIFIED)
            .build();

    public final static Creator CREATOR_2_REQUEST = Creator.builder()
            .pid(CREATOR_2_IDENTIFIER_ID)
            .firstname(CREATOR_2_FIRSTNAME)
            .lastname(CREATOR_2_LASTNAME)
            .created(CREATOR_2_CREATED)
            .lastModified(CREATOR_2_MODIFIED)
            .build();

    public final static CreatorDto CREATOR_2_DTO = CreatorDto.builder()
            .id(CREATOR_2_ID)
            .pid(CREATOR_2_IDENTIFIER_ID)
            .firstname(CREATOR_2_FIRSTNAME)
            .lastname(CREATOR_2_LASTNAME)
            .created(CREATOR_2_CREATED)
            .lastModified(CREATOR_2_MODIFIED)
            .build();

    public final static Long IDENTIFIER_1_ID = 1L;
    public final static Long IDENTIFIER_1_QUERY_ID = 1L;
    public final static String IDENTIFIER_1_DESCRIPTION = "Selecting all from the weather Australia table";
    public final static String IDENTIFIER_1_TITLE = "Australia weather data";
    public final static String IDENTIFIER_1_DOI = "10.1000/182";
    public final static VisibilityType IDENTIFIER_1_VISIBILITY = VisibilityType.SELF;
    public final static VisibilityTypeDto IDENTIFIER_1_VISIBILITY_DTO = VisibilityTypeDto.SELF;
    public final static Instant IDENTIFIER_1_CREATED = Instant.now();
    public final static Instant IDENTIFIER_1_MODIFIED = Instant.now().minus(1, HOURS);

    public final static Identifier IDENTIFIER_1 = Identifier.builder()
            .id(IDENTIFIER_1_ID)
            .qid(IDENTIFIER_1_QUERY_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .doi(IDENTIFIER_1_DOI)
            .visibility(IDENTIFIER_1_VISIBILITY)
            .created(IDENTIFIER_1_CREATED)
            .lastModified(IDENTIFIER_1_MODIFIED)
            .creators(List.of(CREATOR_1, CREATOR_2))
            .build();

    public final static Identifier IDENTIFIER_1_REQUEST = Identifier.builder()
            .qid(IDENTIFIER_1_QUERY_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .doi(IDENTIFIER_1_DOI)
            .visibility(IDENTIFIER_1_VISIBILITY)
            .created(IDENTIFIER_1_CREATED)
            .lastModified(IDENTIFIER_1_MODIFIED)
            .creators(List.of(CREATOR_1_REQUEST, CREATOR_2_REQUEST))
            .build();

    public final static IdentifierDto IDENTIFIER_1_DTO = IdentifierDto.builder()
            .id(IDENTIFIER_1_ID)
            .qid(IDENTIFIER_1_QUERY_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .doi(IDENTIFIER_1_DOI)
            .visibility(IDENTIFIER_1_VISIBILITY_DTO)
            .created(IDENTIFIER_1_CREATED)
            .lastModified(IDENTIFIER_1_MODIFIED)
            .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
            .build();

    public final static IdentifierDto IDENTIFIER_1_DTO_REQUEST = IdentifierDto.builder()
            .qid(IDENTIFIER_1_QUERY_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .doi(IDENTIFIER_1_DOI)
            .visibility(IDENTIFIER_1_VISIBILITY_DTO)
            .created(IDENTIFIER_1_CREATED)
            .lastModified(IDENTIFIER_1_MODIFIED)
            .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
            .build();

    public final static Long QUERY_1_ID = 1L;
    public final static String QUERY_1_TITLE = "All Raindata";
    public final static String QUERY_1_QUERY = "SELECT * FROM rainfall;";
    public final static String QUERY_1_QUERY_NORMALIZED = "SELECT id,rainfall FROM rainfall;";
    public final static String QUERY_1_HASH = "a5ddf5ac87b72173f75ccbd134ba1072";
    public final static Instant QUERY_1_EXECUTION_TIMESTAMP = Instant.now();

    public final static String CREATOR_1_NAME = "First1 Last1";
    public final static String CREATOR_1_AFFIL = "TU Wien";
    public final static String CREATOR_1_ORCID = "0000-0002-5713-0725";

    public final static String CREATOR_2_NAME = "First2 Last2";
    public final static String CREATOR_2_AFFIL = "TU Graz";
    public final static String CREATOR_2_ORCID = "0000-0002-2606-4059";

    public final static String METADATA_1_TITLE = "My super dataset";
    public final static String METADATA_1_DESCRIPTION = "The dataset contains 1000 records of ...";
    public final static String[] METADATA_1_CREATORS = new String[]{CREATOR_1_NAME, CREATOR_2_NAME};

    public final static Long DEPOSIT_2_ID = 2L;
    public final static String DEPOSIT_2_TITLE = "Test Document " + RandomStringUtils.randomAlphanumeric(10);
    public final static String DEPOSIT_2_DESCRIPTION = "Test Description " + RandomStringUtils.randomAlphanumeric(100);
    public final static Instant DEPOSIT_2_CREATED = Instant.now().minus(2, ChronoUnit.HOURS);
    public final static Instant DEPOSIT_2_MODIFIED = Instant.now();
    public final static String DEPOSIT_2_STATE = "draft";
    public final static Boolean DEPOSIT_2_SUBMITTED = false;

    public final static String DEPOSIT_1_DOI = "10.5072/zenodo.542201";
    public final static Long DEPOSIT_1_REC_ID = 542201L;

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

    public final static Long CONTAINER_1_ID = 1L;
    public final static String CONTAINER_1_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_1_IMAGE = IMAGE_1;
    public final static String CONTAINER_1_NAME = "fda-userdb-u01";
    public final static String CONTAINER_1_INTERNALNAME = "fda-userdb-u01";
    public final static String CONTAINER_1_DATABASE = "univie";
    public final static String CONTAINER_1_IP = "172.28.0.5";
    public final static Instant CONTAINER_1_CREATED = Instant.now().minus(1, HOURS);

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_1_HASH)
            .containerCreated(CONTAINER_1_CREATED)
            .build();

    public final static Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .name(DATABASE_1_NAME)
            .isPublic(DATABASE_1_PUBLIC)
            .internalName(DATABASE_1_INTERNAL_NAME)
            .exchange(DATABASE_1_EXCHANGE)
            .tables(List.of())
            .build();

    public final static Table TABLE_1 = Table.builder()
            .id(TABLE_1_ID)
            .name(TABLE_1_NAME)
            .internalName(TABLE_1_INTERNAL_NAME)
            .topic(TABLE_1_TOPIC)
            .tdbid(DATABASE_1_ID)
            .build();

    public final static String COLUMN_1_INTERNAL_NAME = "id";
    public final static String COLUMN_2_INTERNAL_NAME = "name";

    public final static Map<String, Object> ROW_1 = new LinkedHashMap<>() {{
        put(COLUMN_1_INTERNAL_NAME, 1L);
        put(COLUMN_2_INTERNAL_NAME, "Foo");
    }};
    public final static Map<String, Object> ROW_2 = new LinkedHashMap<>() {{
        put(COLUMN_1_INTERNAL_NAME, 2L);
        put(COLUMN_2_INTERNAL_NAME, "Bar");
    }};
    public final static Map<String, Object> ROW_3 = new LinkedHashMap<>() {{
        put(COLUMN_1_INTERNAL_NAME, 3L);
        put(COLUMN_2_INTERNAL_NAME, "Baz");
    }};

    public final static QueryResultDto QUERY_1_RESULT = QueryResultDto.builder()
            .id(QUERY_1_ID)
            .result(List.of(ROW_1, ROW_2, ROW_3))
            .build();

}
