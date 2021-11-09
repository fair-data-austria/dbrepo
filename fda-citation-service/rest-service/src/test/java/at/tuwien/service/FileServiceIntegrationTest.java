package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.deposit.files.FileUploadDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.query.File;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.*;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.ResourceUtils;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FileServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ZenodoFileService fileService;

    @Autowired
    private ZenodoMetadataService metadataService;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private QueryRepository queryRepository;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        containerRepository.save(CONTAINER_1);
        databaseRepository.save(DATABASE_1);
        tableRepository.save(TABLE_1);
        queryRepository.save(QUERY_1);
    }

    @Test
    public void createResource_succeeds() throws ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException,
            ZenodoUnavailableException, QueryNotFoundException, RemoteDatabaseException, TableServiceException,
            ZenodoFileException {

        /* integrate */
        final Query deposit = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);

        /* test */
        final File response = fileService.createResource(DATABASE_1_ID, QUERY_1_ID);
    }

    @Test
    public void createResource_largeFile_succeeds() throws IOException, ZenodoApiException, ZenodoNotFoundException,
            ZenodoAuthenticationException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            QueryNotFoundException, RemoteDatabaseException, TableServiceException, ZenodoFileException {
        final MockMultipartFile file = new MockMultipartFile("weatherAUS.csv", FileUtils.readFileToByteArray(
                ResourceUtils.getFile("classpath:csv/weatherAUS.csv")));

        /* request */
        final Query deposit = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        final FileUploadDto request = FileUploadDto.builder()
                .name(FILE_2_NAME)
                .build();

        /* test */
        final File response = fileService.createResource(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(FILE_1_ID, response.getId());
    }

    @Test
    public void listAll_succeeds() {

        /* test */
        final List<File> response = fileService.listResources();
        assertEquals(1, response.size());
    }

    @Test
    public void findResource_noContent_fails() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoNotFoundException, ZenodoAuthenticationException, ZenodoUnavailableException, QueryNotFoundException,
            RemoteDatabaseException, TableServiceException, ZenodoFileException {

        /* request */
        final Query deposit = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        final File fileResponse = fileService.createResource(DATABASE_1_ID, QUERY_1_ID);

        /* test */
        final File findResponse = fileService.findResource(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(fileResponse.getId(), findResponse.getId());
    }

    @Test
    public void deleteRessource_succeeds() throws MetadataDatabaseNotFoundException, ZenodoApiException,
            ZenodoNotFoundException, ZenodoAuthenticationException, ZenodoUnavailableException, QueryNotFoundException,
            RemoteDatabaseException, TableServiceException, ZenodoFileException {

        /* request */
        final Query deposit = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        final File fileResponse = fileService.createResource(DATABASE_1_ID, QUERY_1_ID);

        /* test */
        fileService.deleteResource(DATABASE_1_ID, QUERY_1_ID);
    }

}