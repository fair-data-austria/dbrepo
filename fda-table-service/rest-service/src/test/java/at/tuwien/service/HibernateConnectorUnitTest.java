package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.config.ReadyConfig;
import at.tuwien.service.impl.HibernateConnector;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Log4j2
public class HibernateConnectorUnitTest extends BaseUnitTest {

    @MockBean
    private ReadyConfig readyConfig;

    @Test
    public void isReserved_succeeds() throws IOException {
        final String request = "TIMESTAMP";

        /* test */
        assertTrue(HibernateConnector.isReserved(request));
    }

    @Test
    public void isReserved_fails() throws IOException {
        final String request = "foobar";

        /* test */
        assertFalse(HibernateConnector.isReserved(request));
    }

}
