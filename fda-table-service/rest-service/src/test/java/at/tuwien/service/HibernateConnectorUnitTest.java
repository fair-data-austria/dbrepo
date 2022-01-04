package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCsvDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.HibernateConnector;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Log4j2
public class HibernateConnectorUnitTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

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
