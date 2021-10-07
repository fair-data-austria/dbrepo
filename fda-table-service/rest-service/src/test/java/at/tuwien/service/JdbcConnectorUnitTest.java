package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.service.impl.JdbcConnector;
import at.tuwien.service.impl.MariaDataService;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class JdbcConnectorUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private MariaDataService mariaDataService;

    @Test
    public void isReserved_succeeds() throws IOException {
        assertTrue(mariaDataService.isReserved("table"));
    }

    @Test
    public void isReserved_uppercase_succeeds() throws IOException {
        assertTrue(mariaDataService.isReserved("TABLE"));
    }

    @Test
    public void isReserved_mixed_succeeds() throws IOException {
        assertTrue(mariaDataService.isReserved("tAbLE"));
    }

    @Test
    public void isReserved_inSentence_fails() throws IOException {
        assertFalse(mariaDataService.isReserved("My nice table is nice"));
    }

    @Test
    public void isReserved_contained_fails() throws IOException {
        assertFalse(mariaDataService.isReserved("My nice tableService is nice"));
    }

}


