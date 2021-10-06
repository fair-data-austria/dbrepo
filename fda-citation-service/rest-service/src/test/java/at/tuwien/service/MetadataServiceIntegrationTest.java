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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import java.util.List;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
    @Transactional
    public void listDeposit_succeeds() {

        /* test */
        final List<Query> response = metadataService.listCitations(DATABASE_1_ID, TABLE_1_ID);
        assertEquals(1, response.size());
    }

    @Test
    public void createDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* test */
        final Query response = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        assertNotNull(response.getId());
        assertNotNull(response.getDoi());
    }

    @Test
    public void updateDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            QueryNotFoundException {
        final Query deposit = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);

        /* test */
        final Query response = metadataService.updateCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID, DEPOST_1_REQUEST);
        assertNotNull(response.getId());
    }

    @Test
    @Disabled
    public void publishDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException,
            QueryNotFoundException {
        final Query query = metadataService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        fileService.createResource(DATABASE_1_ID, TABLE_1_ID, query.getId());

        /* integrate */
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(MetadataDto.builder()
                        .title(METADATA_1_TITLE)
                        .uploadType(UploadTypeDto.DATASET)
                        .description(METADATA_1_DESCRIPTION)
                        .creators(METADATA_1_CREATORS)
                        .build())
                .build();
        metadataService.updateCitation(DATABASE_1_ID, TABLE_1_ID, QUERY_1_ID, request);

        /* test */
        final Query response = metadataService.publishCitation(DATABASE_1_ID, TABLE_1_ID, query.getId());
        assertNotNull(response.getId());
        assertEquals(METADATA_1_TITLE, response.getTitle());
        assertNotNull(response.getDoi());
    }

}