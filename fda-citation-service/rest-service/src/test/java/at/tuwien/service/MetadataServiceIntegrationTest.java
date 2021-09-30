package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.Database;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.TableRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MetadataServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoMetadataService zenodoService;

    @MockBean
    private TableRepository tableRepository;

    final Database DATABASE_1 = Database.builder()
            .id(DATABASE_1_ID)
            .build();

    @Test
    public void listDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoUnavailableException {

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        zenodoService.listCitations(DATABASE_1_ID, TABLE_1_ID);
    }

    @Test
    public void createDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));

        /* test */
        final DepositChangeResponseDto response = zenodoService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        Assertions.assertNotNull(response.getId());
    }

    @Test
    public void updateDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException, MetadataDatabaseNotFoundException, ZenodoUnavailableException {

        /* mock */
        when(tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_1_ID))
                .thenReturn(Optional.of(TABLE_1));
        final DepositChangeResponseDto deposit = zenodoService.storeCitation(DATABASE_1_ID, TABLE_1_ID);
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
                .build();

        /* mock */
        final Table TABLE_1 = Table.builder()
                .id(TABLE_1_ID)
                .depositId(deposit.getId())
                .build();

        /* test */
        final DepositChangeResponseDto response2 = zenodoService.updateCitation(DATABASE_1_ID, TABLE_1_ID, request);
        Assertions.assertNotNull(response2.getId());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getTitle());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getMetadata().getTitle());
        Assertions.assertEquals(METADATA_1_DESCRIPTION, response2.getMetadata().getDescription());
    }

}