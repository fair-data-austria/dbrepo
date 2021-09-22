package at.tuwien.gateway;

import at.tuwien.api.zenodo.deposit.DepositDto;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.ZenodoApiException;
import at.tuwien.exception.ZenodoAuthenticationException;
import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class ZenodoUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ZenodoGateway zenodoGateway;

    @MockBean
    private RestTemplate zenodoTemplate;

    @Test
    public void listDeposit_succeeds() throws ZenodoApiException, ZenodoAuthenticationException {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(DepositDto[].class), anyString()))
                .thenReturn(ResponseEntity.ok(new DepositDto[]{DEPOSIT_1_DTO}));

        /* test */
        final List<DepositDto> response = zenodoGateway.listDeposits();
        assertEquals(1, response.size());
        assertEquals(DEPOSIT_1_ID, response.get(0).getId());
    }

    @Test
    public void listDeposit_noToken_fails() {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(DepositDto[].class), anyString()))
                .thenReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());

        /* test */
        assertThrows(ZenodoAuthenticationException.class, () -> {
            zenodoGateway.listDeposits();
        });
    }

    @Test
    public void listDeposit_empty_fails() {
        when(zenodoTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), eq(DepositDto[].class), anyString()))
                .thenReturn(ResponseEntity.ok().build());

        /* test */
        assertThrows(ZenodoApiException.class, () -> {
            zenodoGateway.listDeposits();
        });
    }

}