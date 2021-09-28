package at.tuwien;

import at.tuwien.api.zenodo.deposit.ContributorDto;
import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.api.zenodo.deposit.LicenseTypeDto;
import at.tuwien.api.zenodo.files.FileDto;
import at.tuwien.api.zenodo.files.FileLinksDto;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.LocalDateTime;
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

    public final static String CONTRIBUTOR_1_NAME = "von Goethe, Johann Wolfang";
    public final static String CONTRIBUTOR_1_AFFILIATION = "Universität Leipzig";

    public final static Long DEPOSIT_2_ID = 2L;
    public final static String DEPOSIT_2_TITLE = "Test Document " + RandomStringUtils.randomAlphanumeric(10);
    public final static String DEPOSIT_2_DESCRIPTION = "Test Description " + RandomStringUtils.randomAlphanumeric(100);
    public final static Instant DEPOSIT_2_CREATED = Instant.now().minus(2, ChronoUnit.HOURS);
    public final static Instant DEPOSIT_2_MODIFIED = Instant.now();
    public final static LicenseTypeDto DEPOSIT_2_LICENSE = LicenseTypeDto.CC_BY;
    public final static String DEPOSIT_2_STATE = "draft";
    public final static Boolean DEPOSIT_2_SUBMITTED = false;


    public final static String FILE_1_CHECKSUM = "deadbeef";
    public final static String FILE_1_NAME = "data.csv";
    public final static String FILE_1_ID = "73d5674b-2bda-400c-844e-4eef309afe5e";
    public final static Long FILE_1_SIZE = 3943498L;

    public final static String FILE_1_LINKS_DOWNLOAD = "http://localhost:5500/file/" + FILE_1_ID + "/download";
    public final static String FILE_1_LINKS_SELF = "http://localhost:5500/file/" + FILE_1_ID;

    public final static String DEPOSIT_1_DOI = "10.5072/zenodo.542201";
    public final static Long DEPOSIT_1_REC_ID = 542201L;

    public final static FileLinksDto FILE_1_LINKS_DTO = FileLinksDto.builder()
            .download(FILE_1_LINKS_DOWNLOAD)
            .self(FILE_1_LINKS_SELF)
            .build();

    public final static FileDto FILE_1_DTO = FileDto.builder()
            .checksum(FILE_1_CHECKSUM)
            .filename(FILE_1_NAME)
            .id(FILE_1_ID)
            .filesize(FILE_1_SIZE)
            .links(FILE_1_LINKS_DTO)
            .build();

    public final static DepositDto DEPOSIT_1_RETURN_DTO = DepositDto.builder()
            .id(DEPOSIT_1_ID)
            .title(DEPOSIT_1_TITLE)
            .state(DEPOSIT_1_STATE)
            .submitted(DEPOSIT_1_SUBMITTED)
            .recordId(DEPOSIT_1_RECORD_ID)
            .files(List.of(FILE_1_DTO))
            .build();

    public final static ContributorDto CONTRIBUTOR_1_DTO = ContributorDto.builder()
            .name(CONTRIBUTOR_1_NAME)
            .affiliation(CONTRIBUTOR_1_AFFILIATION)
            .build();

}
