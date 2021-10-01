package at.tuwien;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileLinksDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItem;
import at.tuwien.entities.container.image.ContainerImageEnvironmentItemType;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    public final static Long DEPOSIT_1_ID = 1L;
    public final static String DEPOSIT_1_TITLE = "Super cool document";
    public final static String DEPOSIT_1_DESCRIPTION = "My document is the best";
    public final static Instant DEPOSIT_1_CREATED = Instant.now().minus(1, ChronoUnit.HOURS);
    public final static Instant DEPOSIT_1_MODIFIED = Instant.now();
    public final static String DEPOSIT_1_STATE = "unsubmitted";
    public final static Boolean DEPOSIT_1_SUBMITTED = false;
    public final static Long DEPOSIT_1_RECORD_ID = 1899L;
    public final static LicenseTypeDto DEPOSIT_1_LICENSE = LicenseTypeDto.BSD;
    public final static Long DEPOSIT_1_CONCEPT_RECORD_ID = 143L;
    public final static Long DEPOSIT_1_OWNER = 144L;

    public final static String CREATOR_1_NAME = "First1 Last1";
    public final static String CREATOR_1_AFFIL = "TU Wien";
    public final static String CREATOR_1_ORCID = "0000-0002-5713-0725";

    public final static CreatorDto CREATOR_1 = CreatorDto.builder()
            .name(CREATOR_1_NAME)
            .affiliation(CREATOR_1_AFFIL)
            .orcid(CREATOR_1_ORCID)
            .build();

    public final static String CREATOR_2_NAME = "First2 Last2";
    public final static String CREATOR_2_AFFIL = "TU Graz";
    public final static String CREATOR_2_ORCID = "0000-0002-2606-4059";

    public final static CreatorDto CREATOR_2 = CreatorDto.builder()
            .name(CREATOR_2_NAME)
            .affiliation(CREATOR_2_AFFIL)
            .orcid(CREATOR_2_ORCID)
            .build();

    public final static String METADATA_1_TITLE = "My super dataset";
    public final static UploadTypeDto METADATA_1_UPLOAD_TYPE = UploadTypeDto.DATASET;
    public final static String METADATA_1_DESCRIPTION = "The dataset contains 1000 records of ...";
    public final static CreatorDto[] METADATA_1_CREATORS = new CreatorDto[] {CREATOR_1, CREATOR_2};

    public final static String FILE_1_ID = "deadbeef-deafdeed";
    public final static String FILE_1_NAME = "testdata-othername.csv";
    public final static String FILE_1_CHECKSUM = "d393c7fa1240c18473133793f7901aaa";
    public final static Long FILE_1_SIZE = 34614L;

    public final static String FILE_2_ID = "deadbeef-deafdeed";
    public final static String FILE_2_NAME = "testdata-weather.csv";
    public final static String FILE_2_CHECKSUM = "a65cf8b8719b1a65db4f361eeec18457";
    public final static Long FILE_2_SIZE = 14094055L;

    public final static Long DEPOSIT_2_ID = 2L;
    public final static String DEPOSIT_2_TITLE = "Test Document " + RandomStringUtils.randomAlphanumeric(10);
    public final static String DEPOSIT_2_DESCRIPTION = "Test Description " + RandomStringUtils.randomAlphanumeric(100);
    public final static Instant DEPOSIT_2_CREATED = Instant.now().minus(2, ChronoUnit.HOURS);
    public final static Instant DEPOSIT_2_MODIFIED = Instant.now();
    public final static LicenseTypeDto DEPOSIT_2_LICENSE = LicenseTypeDto.CC_BY;
    public final static String DEPOSIT_2_STATE = "draft";
    public final static Boolean DEPOSIT_2_SUBMITTED = false;

    public final static String FILE_1_LINKS_DOWNLOAD = "http://localhost:5500/file/" + FILE_1_ID + "/download";
    public final static String FILE_1_LINKS_SELF = "http://localhost:5500/file/" + FILE_1_ID;

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
            .depositId(DEPOSIT_1_ID)
            .build();

    public final static MetadataDto METADATA_1 = MetadataDto.builder()
            .creators(new CreatorDto[]{CREATOR_1, CREATOR_2})
            .description(METADATA_1_DESCRIPTION)
            .title(METADATA_1_TITLE)
            .uploadType(METADATA_1_UPLOAD_TYPE)
            .build();

    public final static FileLinksDto FILE_1_LINKS = FileLinksDto.builder()
            .download(FILE_1_LINKS_DOWNLOAD)
            .self(FILE_1_LINKS_SELF)
            .build();

    public final static FileResponseDto FILE_1 = FileResponseDto.builder()
            .checksum(FILE_1_CHECKSUM)
            .filename(FILE_1_NAME)
            .id(FILE_1_ID)
            .filesize(FILE_1_SIZE)
            .links(FILE_1_LINKS)
            .build();

    public final static DepositChangeResponseDto DEPOSIT_1 = DepositChangeResponseDto.builder()
            .id(DEPOSIT_1_ID)
            .created(DEPOSIT_1_CREATED)
            .modified(DEPOSIT_1_MODIFIED)
            .title(DEPOSIT_1_TITLE)
            .state(DEPOSIT_1_STATE)
            .submitted(DEPOSIT_1_SUBMITTED)
            .recordId(DEPOSIT_1_RECORD_ID)
            .files(List.of(FILE_1))
            .build();

    public final static DepositResponseDto DEPOSIT_2 = DepositResponseDto.builder()
            .id(DEPOSIT_1_ID)
            .title(DEPOSIT_1_TITLE)
            .state(DEPOSIT_1_STATE)
            .submitted(DEPOSIT_1_SUBMITTED)
            .recordId(DEPOSIT_1_RECORD_ID)
            .files(List.of(FILE_1))
            .build();

}
