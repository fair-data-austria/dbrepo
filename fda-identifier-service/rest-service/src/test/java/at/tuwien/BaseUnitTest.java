package at.tuwien;

import at.tuwien.api.database.query.QueryDto;
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
import static java.time.temporal.ChronoUnit.MINUTES;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static String GATEWAY_SERVICE_REPOSITORY = "fda-gateway-service:latest";
    public final static String GATEWAY_SERVICE_INTERNAL_NAME = "fda-gateway-service";
    public final static String GATEWAY_SERVICE_IP = "172.29.0.4";
    public final static String[] GATEWAY_SERVICE_ENV = new String[]{"SPRING_PROFILES_ACTIVE=docker"};

    public final static String DISCOVERY_SERVICE_REPOSITORY = "fda-discovery-service:latest";
    public final static String DISCOVERY_SERVICE_INTERNAL_NAME = "fda-discovery-service";
    public final static String DISCOVERY_SERVICE_IP = "172.29.0.5";
    public final static String[] DISCOVERY_SERVICE_ENV = new String[]{"SPRING_PROFILES_ACTIVE=docker"};

    public final static String QUERY_SERVICE_REPOSITORY = "fda-query-service:latest";
    public final static String QUERY_SERVICE_INTERNAL_NAME = "fda-query-service";
    public final static String QUERY_SERVICE_IP = "172.29.0.6";
    public final static String[] QUERY_SERVICE_ENV = new String[]{"SPRING_PROFILES_ACTIVE=docker"};

    public final static String METADATA_DB_REPOSITORY = "fda-metadata-db:latest";
    public final static String METADATA_DB_INTERNAL_NAME = "fda-metadata-db";
    public final static String METADATA_DB_IP = "172.29.0.7";
    public final static String[] METADATA_DB_ENV = new String[]{"POSTGRES_USER=postgres", "POSTGRES_PASSWORD=postgres", "POSTGRES_DB=fda"};

    public final static Long DATABASE_1_ID = 1L;
    public final static String DATABASE_1_NAME = "Test Database";
    public final static String DATABASE_1_INTERNAL_NAME = "test_database";
    public final static String DATABASE_1_EXCHANGE = "fda." + DATABASE_1_INTERNAL_NAME;
    public final static Boolean DATABASE_1_PUBLIC = true;

    public final static Long DATABASE_2_ID = 2L;
    public final static String DATABASE_2_NAME = "Test Database 2";
    public final static String DATABASE_2_INTERNAL_NAME = "test_database_2";
    public final static String DATABASE_2_EXCHANGE = "fda." + DATABASE_2_INTERNAL_NAME;
    public final static Boolean DATABASE_2_PUBLIC = true;

    public final static Long TABLE_1_ID = 1L;
    public final static String TABLE_1_NAME = "Rainfall";
    public final static String TABLE_1_INTERNAL_NAME = "rainfall";
    public final static String TABLE_1_TOPIC = DATABASE_1_EXCHANGE + "." + TABLE_1_INTERNAL_NAME;

    public final static Long CREATOR_1_ID = 1L;
    public final static Long CREATOR_1_QUERY_ID = 1L;
    public final static Long CREATOR_1_IDENTIFIER_ID = 1L;
    public final static String CREATOR_1_FIRSTNAME = "Max";
    public final static String CREATOR_1_LASTNAME = "Mustermann";
    public final static Instant CREATOR_1_CREATED = Instant.ofEpochSecond(1641588352);
    public final static Instant CREATOR_1_MODIFIED = Instant.ofEpochSecond(1541588352);

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
    public final static Long CREATOR_2_QUERY_ID = 1L;
    public final static Long CREATOR_2_IDENTIFIER_ID = 1L;
    public final static String CREATOR_2_FIRSTNAME = "Martina";
    public final static String CREATOR_2_LASTNAME = "Mustermann";
    public final static Instant CREATOR_2_CREATED = Instant.ofEpochSecond(1641588352);
    public final static Instant CREATOR_2_MODIFIED = Instant.ofEpochSecond(1541588352);

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

    public final static String CREATOR_1_NAME = "First1 Last1";
    public final static String CREATOR_1_AFFIL = "TU Wien";
    public final static String CREATOR_1_ORCID = "0000-0002-5713-0725";

    public final static String CREATOR_2_NAME = "First2 Last2";
    public final static String CREATOR_2_AFFIL = "TU Graz";
    public final static String CREATOR_2_ORCID = "0000-0002-2606-4059";

    public final static String METADATA_1_TITLE = "My super dataset";
    public final static String METADATA_1_DESCRIPTION = "The dataset contains 1000 records of ...";
    public final static String[] METADATA_1_CREATORS = new String[]{CREATOR_1_NAME, CREATOR_2_NAME};

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
    public final static Instant IMAGE_1_BUILT = Instant.ofEpochSecond(1441588352);
    public final static List<ContainerImageEnvironmentItem> IMAGE_1_ENV = List.of(ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_1_ID)
                    .key("POSTGRES_USER")
                    .value("postgres")
                    .type(ContainerImageEnvironmentItemType.USERNAME)
                    .build(),
            ContainerImageEnvironmentItem.builder()
                    .iid(IMAGE_1_ID)
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
    public final static Instant CONTAINER_1_CREATED = Instant.ofEpochSecond(1641588352);

    public final static Long CONTAINER_2_ID = 2L;
    public final static String CONTAINER_2_HASH = "deadbeef";
    public final static ContainerImage CONTAINER_2_IMAGE = IMAGE_1;
    public final static String CONTAINER_2_NAME = "fda-userdb-u02";
    public final static String CONTAINER_2_INTERNALNAME = "fda-userdb-u02";
    public final static String CONTAINER_2_DATABASE = "univie";
    public final static String CONTAINER_2_IP = "172.28.0.6";
    public final static Instant CONTAINER_2_CREATED = Instant.ofEpochSecond(1641588352);

    public final static Container CONTAINER_1 = Container.builder()
            .id(CONTAINER_1_ID)
            .name(CONTAINER_1_NAME)
            .internalName(CONTAINER_1_INTERNALNAME)
            .image(CONTAINER_1_IMAGE)
            .hash(CONTAINER_1_HASH)
            .build();

    public final static Container CONTAINER_2 = Container.builder()
            .id(CONTAINER_2_ID)
            .name(CONTAINER_2_NAME)
            .internalName(CONTAINER_2_INTERNALNAME)
            .image(CONTAINER_2_IMAGE)
            .hash(CONTAINER_2_HASH)
            .build();

    public final static Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .name(DATABASE_1_NAME)
            .isPublic(DATABASE_1_PUBLIC)
            .internalName(DATABASE_1_INTERNAL_NAME)
            .exchange(DATABASE_1_EXCHANGE)
            .tables(List.of())
            .build();

    public final static Database DATABASE_2 = Database.builder()
            .id(DATABASE_2_ID)
            .name(DATABASE_2_NAME)
            .isPublic(DATABASE_2_PUBLIC)
            .internalName(DATABASE_2_INTERNAL_NAME)
            .exchange(DATABASE_2_EXCHANGE)
            .tables(List.of())
            .build();

    public final static Table TABLE_1 = Table.builder()
            .id(TABLE_1_ID)
            .name(TABLE_1_NAME)
            .internalName(TABLE_1_INTERNAL_NAME)
            .topic(TABLE_1_TOPIC)
            .tdbid(DATABASE_1_ID)
            .build();

    public final static Long QUERY_1_ID = 1L;
    public final static Long QUERY_1_CONTAINER_ID = CONTAINER_1_ID;
    public final static Long QUERY_1_DATABASE_ID = DATABASE_1_ID;
    public final static String QUERY_1_STATEMENT = "SELECT * FROM `weather`;";
    public final static String QUERY_1_RESULT_HASH = "ff3f7cbe1b96d296957f6e39e55b8b1b577fa3d205d4795af99594cfd20cb80d";
    public final static Long QUERY_1_RESULT_NUMBER = 9L;
    public final static Instant QUERY_1_CREATED = Instant.ofEpochSecond(1641588352);
    public final static Instant QUERY_1_LAST_MODIFIED = Instant.ofEpochSecond(1541588352);

    public final static QueryDto QUERY_1_DTO = QueryDto.builder()
            .id(QUERY_1_ID)
            .cid(QUERY_1_CONTAINER_ID)
            .dbid(QUERY_1_DATABASE_ID)
            .query(QUERY_1_STATEMENT)
            .queryNormalized(QUERY_1_STATEMENT)
            .resultNumber(QUERY_1_RESULT_NUMBER)
            .resultHash(QUERY_1_RESULT_HASH)
            .lastModified(QUERY_1_LAST_MODIFIED)
            .created(QUERY_1_CREATED)
            .build();

    public final static Long QUERY_2_ID = 2L;
    public final static Long QUERY_2_CONTAINER_ID = CONTAINER_2_ID;
    public final static Long QUERY_2_DATABASE_ID = DATABASE_2_ID;
    public final static String QUERY_2_STATEMENT = "SELECT * FROM `weather`;";
    public final static String QUERY_2_RESULT_HASH = "ff3f7cbe1b96d296957f6e39e55b8b1b577fa3d205d4795af99594cfd20cb80d";
    public final static Long QUERY_2_RESULT_NUMBER = 5L;
    public final static Instant QUERY_2_CREATED = Instant.ofEpochSecond(1641588352);
    public final static Instant QUERY_2_LAST_MODIFIED = Instant.ofEpochSecond(1541588352);

    public final static QueryDto QUERY_2_DTO = QueryDto.builder()
            .id(QUERY_2_ID)
            .cid(QUERY_2_CONTAINER_ID)
            .dbid(QUERY_2_DATABASE_ID)
            .query(QUERY_2_STATEMENT)
            .queryNormalized(QUERY_2_STATEMENT)
            .resultNumber(QUERY_2_RESULT_NUMBER)
            .resultHash(QUERY_2_RESULT_HASH)
            .lastModified(QUERY_2_LAST_MODIFIED)
            .created(QUERY_2_CREATED)
            .build();

    public final static Long IDENTIFIER_1_ID = 1L;
    public final static Long IDENTIFIER_1_QUERY_ID = QUERY_1_ID;
    public final static Long IDENTIFIER_1_DATABASE_ID = DATABASE_1_ID;
    public final static String IDENTIFIER_1_DESCRIPTION = "Selecting all from the weather Australia table";
    public final static String IDENTIFIER_1_TITLE = "Australia weather data";
    public final static String IDENTIFIER_1_DOI = "10.1000/182";
    public final static VisibilityType IDENTIFIER_1_VISIBILITY = VisibilityType.SELF;
    public final static VisibilityTypeDto IDENTIFIER_1_VISIBILITY_DTO = VisibilityTypeDto.SELF;
    public final static Instant IDENTIFIER_1_CREATED = Instant.ofEpochSecond(1641588352);
    public final static Instant IDENTIFIER_1_MODIFIED = Instant.ofEpochSecond(1541588352);

    public final static Long IDENTIFIER_2_ID = 2L;
    public final static Long IDENTIFIER_2_QUERY_ID = QUERY_2_ID;
    public final static Long IDENTIFIER_2_DATABASE_ID = DATABASE_1_ID;
    public final static String IDENTIFIER_2_DESCRIPTION = "Selecting all from the weather Austria table";
    public final static String IDENTIFIER_2_TITLE = "Austria weather data";
    public final static String IDENTIFIER_2_DOI = "10.1000/183";
    public final static VisibilityType IDENTIFIER_2_VISIBILITY = VisibilityType.SELF;
    public final static VisibilityTypeDto IDENTIFIER_2_VISIBILITY_DTO = VisibilityTypeDto.SELF;
    public final static Instant IDENTIFIER_2_CREATED = Instant.ofEpochSecond(1641588352);
    public final static Instant IDENTIFIER_2_MODIFIED = Instant.ofEpochSecond(1541588352);

    public final static Identifier IDENTIFIER_1 = Identifier.builder()
            .id(IDENTIFIER_1_ID)
            .qid(IDENTIFIER_1_QUERY_ID)
            .dbid(IDENTIFIER_1_DATABASE_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .doi(IDENTIFIER_1_DOI)
            .visibility(IDENTIFIER_1_VISIBILITY)
            .created(IDENTIFIER_1_CREATED)
            .lastModified(IDENTIFIER_1_MODIFIED)
            .build();

    public final static Identifier IDENTIFIER_2 = Identifier.builder()
            .id(IDENTIFIER_2_ID)
            .qid(IDENTIFIER_2_QUERY_ID)
            .dbid(IDENTIFIER_2_DATABASE_ID)
            .description(IDENTIFIER_2_DESCRIPTION)
            .title(IDENTIFIER_2_TITLE)
            .doi(IDENTIFIER_2_DOI)
            .visibility(IDENTIFIER_2_VISIBILITY)
            .created(IDENTIFIER_2_CREATED)
            .lastModified(IDENTIFIER_2_MODIFIED)
            .build();

    public final static Identifier IDENTIFIER_1_REQUEST = Identifier.builder()
            .qid(IDENTIFIER_1_QUERY_ID)
            .dbid(IDENTIFIER_1_DATABASE_ID)
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
            .dbid(IDENTIFIER_1_DATABASE_ID)
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
            .dbid(IDENTIFIER_1_DATABASE_ID)
            .description(IDENTIFIER_1_DESCRIPTION)
            .title(IDENTIFIER_1_TITLE)
            .doi(IDENTIFIER_1_DOI)
            .visibility(IDENTIFIER_1_VISIBILITY_DTO)
            .created(IDENTIFIER_1_CREATED)
            .lastModified(IDENTIFIER_1_MODIFIED)
            .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
            .build();

    public final static IdentifierDto IDENTIFIER_2_DTO_REQUEST = IdentifierDto.builder()
            .qid(IDENTIFIER_2_QUERY_ID)
            .dbid(IDENTIFIER_2_DATABASE_ID)
            .description(IDENTIFIER_2_DESCRIPTION)
            .title(IDENTIFIER_2_TITLE)
            .doi(IDENTIFIER_2_DOI)
            .visibility(IDENTIFIER_2_VISIBILITY_DTO)
            .created(IDENTIFIER_2_CREATED)
            .lastModified(IDENTIFIER_2_MODIFIED)
            .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
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
