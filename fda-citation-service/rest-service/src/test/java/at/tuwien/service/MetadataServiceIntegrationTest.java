package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.api.zenodo.files.FileUploadDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MetadataServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private MetadataService metadataService;

    @Autowired
    private FileService fileService;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        containerRepository.save(CONTAINER_1);
        databaseRepository.save(DATABASE_1);
        tableRepository.save(TABLE_1);
    }

    @Test
    public void listDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoUnavailableException, MetadataDatabaseNotFoundException {

        /* test */
        metadataService.listCitations(DATABASE_1_ID, TABLE_1_ID);
    }

    @Test
    public void createDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* test */
        final DepositChangeResponseDto response = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        Assertions.assertNotNull(response.getId());
        Assertions.assertNotNull(response.getMetadata().getPrereserveDoi().getDoi());
        final Optional<Table> persistence = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        Assertions.assertTrue(persistence.isPresent());
        Assertions.assertNotNull(persistence.get().getDepositId());
    }

    @Test
    public void updateDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* test */
        final DepositChangeResponseDto response2 = metadataService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
        Assertions.assertNotNull(response2.getId());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getTitle());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getMetadata().getTitle());
        Assertions.assertEquals(METADATA_1_DESCRIPTION, response2.getMetadata().getDescription());
        final Optional<Table> persistence = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        Assertions.assertTrue(persistence.isPresent());
        Assertions.assertNotNull(persistence.get().getDepositId());
    }

    @Test
    public void publishDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException, IOException,
            ZenodoFileTooLargeException {
        final MockMultipartFile file = new MockMultipartFile("testdata.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/testdata.csv")));
        final DepositChangeResponseDto deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_1_NAME)
                .build();
        fileService.createResource(DATABASE_1_ID, TABLE_1_ID, request, file);

        /* update */
        final DepositChangeRequestDto request2 = DepositChangeRequestDto.builder()
                .metadata(MetadataDto.builder()
                        .title(METADATA_1_TITLE)
                        .uploadType(UploadTypeDto.DATASET)
                        .description(METADATA_1_DESCRIPTION)
                        .creators(METADATA_1_CREATORS)
                        .build())
                .build();
        final DepositChangeResponseDto response = metadataService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request2);

        /* test */
        final DepositChangeResponseDto response2 = metadataService.publishCitation(DATABASE_1_ID, TABLE_1_ID);
        Assertions.assertNotNull(response2.getId());
        Assertions.assertTrue(response2.getSubmitted());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getTitle());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getMetadata().getTitle());
        Assertions.assertEquals(METADATA_1_DESCRIPTION, response2.getMetadata().getDescription());
        Assertions.assertNotNull(response2.getMetadata().getPrereserveDoi().getDoi());

        final Optional<Table> persistence = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID);
        Assertions.assertTrue(persistence.isPresent());
        Assertions.assertNotNull(persistence.get().getDepositId());
    }

}