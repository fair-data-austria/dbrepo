package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ZenodoServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoService zenodoService;

    @Autowired
    private RestTemplate zenodoTemplate;

    @Test
    public void listDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {

        /* test */
        zenodoService.listCitations();
    }

    @Test
    public void createDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {

        /* test */
        final DepositDto response = zenodoService.storeCitation();
        Assertions.assertNotNull(response.getId());
    }

}