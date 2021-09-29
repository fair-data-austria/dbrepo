package at.tuwien;

import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.api.zenodo.files.FileResponseDto;
import at.tuwien.api.zenodo.files.FileLinksDto;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

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

    public final static String METADATA_1_TITLE = "My super dataset";
    public final static UploadTypeDto METADATA_1_UPLOAD_TYPE = UploadTypeDto.DATASET;
    public final static String METADATA_1_DESCRIPTION = "The dataset contains 1000 records of ...";

    public final static String CREATOR_1_NAME = "First1 Last1";
    public final static String CREATOR_1_AFFIL = "TU Wien";
    public final static String CREATOR_1_ORCID = "0000-0002-5713-0725";

    public final static String CREATOR_2_NAME = "First2 Last2";
    public final static String CREATOR_2_AFFIL = "TU Graz";
    public final static String CREATOR_2_ORCID = "0000-0002-2606-4059";

    public final static String FILE_1_ID = "deadbeef-deafdeed";
    public final static String FILE_1_NAME = "testdata-othername.csv";
    public final static String FILE_1_CHECKSUM = "d393c7fa1240c18473133793f7901aaa";
    public final static Long FILE_1_SIZE = 34614L;

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

    public final static CreatorDto CREATOR_1 = CreatorDto.builder()
            .name(CREATOR_1_NAME)
            .affiliation(CREATOR_1_AFFIL)
            .orcid(CREATOR_1_ORCID)
            .build();

    public final static CreatorDto CREATOR_2 = CreatorDto.builder()
            .name(CREATOR_2_NAME)
            .affiliation(CREATOR_2_AFFIL)
            .orcid(CREATOR_2_ORCID)
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
