package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.deposit.DepositChangeRequestDto;
import at.tuwien.api.database.deposit.metadata.MetadataDto;
import at.tuwien.api.database.deposit.metadata.UploadTypeDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.query.Query;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.QueryRepository;
import at.tuwien.repository.jpa.TableRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
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

    @Autowired
    private QueryRepository queryRepository;

    @BeforeEach
    @Transactional
    public void beforeEach() {
        containerRepository.save(CONTAINER_1);
        databaseRepository.save(DATABASE_1);
        queryRepository.save(QUERY_1);
    }

    @Test
    @Transactional
    public void listDeposit_succeeds() throws MetadataDatabaseNotFoundException {

        /* test */
        final List<Query> response = metadataService.listCitations(DATABASE_1_ID);
        assertEquals(1, response.size());
    }

    @Test
    public void createDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* test */
        final Query response = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(QUERY_1_ID, response.getId());
        assertNotNull(response.getDoi());
    }

    @Test
    public void updateDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            QueryNotFoundException {
        final Query query = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        assertNotNull(query.getDepositId());

        /* test */
        final Query response = metadataService.updateCitation(DATABASE_1_ID, QUERY_1_ID, DEPOST_1_REQUEST);
        assertEquals(QUERY_1_ID, response.getId());
        assertNotNull(response.getDepositId());
        assertEquals(DATABASE_1_ID, response.getQdbid());
    }

    @Test
    @Disabled("something missing in query service")
    public void publishDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            QueryNotFoundException, RemoteDatabaseException, TableServiceException, ZenodoFileException {
        final Query query = metadataService.storeCitation(DATABASE_1_ID, QUERY_1_ID);
        assertNull(query.getId());
        assertNotNull(query.getDepositId());
        fileService.createResource(DATABASE_1_ID, QUERY_1_ID);

        /* integrate */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(MetadataDto.builder()
                        .title(METADATA_1_TITLE)
                        .uploadType(UploadTypeDto.DATASET)
                        .description(METADATA_1_DESCRIPTION)
                        .creators(METADATA_1_CREATORS)
                        .build())
                .build();
        metadataService.updateCitation(DATABASE_1_ID, QUERY_1_ID, request);

        /* test */
        final Query response = metadataService.publishCitation(DATABASE_1_ID, query.getId());
        assertNotNull(response.getId());
        assertEquals(METADATA_1_TITLE, response.getTitle());
        assertNotNull(response.getDoi());
    }

}