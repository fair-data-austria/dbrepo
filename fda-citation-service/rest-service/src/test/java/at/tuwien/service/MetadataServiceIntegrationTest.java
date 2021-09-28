package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.*;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import at.tuwien.exception.ZenodoNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class MetadataServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoMetadataService zenodoService;

    @Test
    public void listDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {

        /* test */
        zenodoService.listCitations();
    }

    @Test
    public void createDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {

        /* test */
        final DepositChangeResponseDto response = zenodoService.storeCitation();
        Assertions.assertNotNull(response.getId());
    }

    @Test
    public void updateDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException,
            ZenodoNotFoundException {
        final DepositChangeRequestDto request = DepositChangeRequestDto.builder()
                .metadata(METADATA_1)
               .build();
        final DepositChangeResponseDto response = zenodoService.storeCitation();

        /* test */
        final DepositChangeResponseDto response2 = zenodoService.updateCitation(response.getId(), request);
        Assertions.assertNotNull(response2.getId());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getTitle());
        Assertions.assertEquals(METADATA_1_TITLE, response2.getMetadata().getTitle());
        Assertions.assertEquals(METADATA_1_DESCRIPTION, response2.getMetadata().getDescription());
    }

}