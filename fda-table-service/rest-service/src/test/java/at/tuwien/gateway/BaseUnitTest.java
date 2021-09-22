package at.tuwien.gateway;

import at.tuwien.api.zenodo.MetadataDto;
import at.tuwien.api.zenodo.PreserveDoiDto;
import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.api.zenodo.files.FileDto;
import at.tuwien.api.zenodo.files.FileLinksDto;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@TestPropertySource(locations = "classpath:application.properties")
public abstract class BaseUnitTest {

    public final static Long DEPOSIT_1_ID = 1L;
    public final static String DEPOSIT_1_TITLE = "Super cool document";
    public final static Instant DEPOSIT_1_CREATED = Instant.now().minus(1, ChronoUnit.HOURS);
    public final static Instant DEPOSIT_1_MODIFIED = Instant.now();
    public final static String DEPOSIT_1_STATE = "draft";
    public final static Boolean DEPOSIT_1_SUBMITTED = false;
    public final static Long DEPOSIT_1_RECORD_ID = 1899L;
    public final static Long DEPOSIT_1_CONCEPT_RECORD_ID = 143L;
    public final static Long DEPOSIT_1_OWNER = 144L;

    public final static String FILE_1_CHECKSUM = "deadbeef";
    public final static String FILE_1_NAME = "data.csv";
    public final static String FILE_1_ID = "73d5674b-2bda-400c-844e-4eef309afe5e";
    public final static Long FILE_1_SIZE = 3943498L;

    public final static String FILE_1_LINKS_DOWNLOAD = "http://localhost:5500/file/" + FILE_1_ID + "/download";
    public final static String FILE_1_LINKS_SELF = "http://localhost:5500/file/" + FILE_1_ID;

    public final static String DEPOSIT_1_DOI = "10.5072/zenodo.542201";
    public final static Long DEPOSIT_1_REC_ID = 542201L;

    public final static PreserveDoiDto DEPOSIT_1_PRESERVE_DOI = PreserveDoiDto.builder()
            .doi(DEPOSIT_1_DOI)
            .recId(DEPOSIT_1_REC_ID)
            .build();

    public final static MetadataDto FILE_1_METADATA_DTO = MetadataDto.builder()
            .prereserveDoi(DEPOSIT_1_PRESERVE_DOI)
            .build();

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

    public final static DepositDto DEPOSIT_1_DTO = DepositDto.builder()
            .id(DEPOSIT_1_ID)
            .title(DEPOSIT_1_TITLE)
            .created(DEPOSIT_1_CREATED)
            .state(DEPOSIT_1_STATE)
            .submitted(DEPOSIT_1_SUBMITTED)
            .conceptRecId(DEPOSIT_1_CONCEPT_RECORD_ID)
            .owner(DEPOSIT_1_OWNER)
            .modified(DEPOSIT_1_MODIFIED)
            .recordId(DEPOSIT_1_RECORD_ID)
            .files(List.of(FILE_1_DTO))
            .metadata(FILE_1_METADATA_DTO)
            .build();

}
