package at.tuwien.endpoint;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.*;
import at.tuwien.config.ReadyConfig;
import at.tuwien.endpoints.DataEndpoint;
import at.tuwien.endpoints.TableEndpoint;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.DataService;
import at.tuwien.service.JdbcConnector;
import at.tuwien.service.TableService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DataEndpointIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DataService dataService;

    @Autowired
    private TableService tableService;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private DataEndpoint dataEndpoint;

    private CreateContainerResponse request;

    @Transactional
    @BeforeEach
    public void beforeEach() throws InterruptedException, TableMalformedException, ArbitraryPrimaryKeysException,
            DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException {
        afterEach();
        /* create network */
        dockerClient.createNetworkCmd()
                .withName("fda-userdb")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        imageRepository.save(IMAGE_1);
        /* create container */
        request = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withEnv(IMAGE_1_ENVIRONMENT)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .exec();
        /* start container */
        dockerClient.startContainerCmd(request.getId()).exec();
        Thread.sleep(3000);
        databaseRepository.save(DATABASE_1);
        tableService.createTable(DATABASE_1_ID, TABLE_1_CREATE_DTO);
        tableRepository.save(TABLE_1);
    }

    @AfterEach
    public void afterEach() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    log.info("Delete container {}", Arrays.asList(container.getNames()));
                    try {
                        dockerClient.stopContainerCmd(container.getId()).exec();
                    } catch (NotModifiedException e) {
                        // ignore
                    }
                    dockerClient.removeContainerCmd(container.getId()).exec();
                });
        /* remove networks */
        dockerClient.listNetworksCmd()
                .exec()
                .stream()
                .filter(n -> n.getName().startsWith("fda"))
                .forEach(network -> {
                    log.info("Delete network {}", network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    @Test
    @Disabled
    public void insertFromTuple_succeeds() {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(Map.of(COLUMN_1_NAME, 1L, COLUMN_2_NAME, Instant.now(), COLUMN_3_NAME, 35.2,
                        COLUMN_4_NAME, "Sydney", COLUMN_5_NAME, 10.2)))
                .build();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insertFromTuple(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromTuple_empty_fails() {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of())
                .build();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insertFromTuple(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void insertFromTuple_empty2_fails() {
        final TableCsvDto request = TableCsvDto.builder()
                .data(List.of(Map.of()))
                .build();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataEndpoint.insertFromTuple(DATABASE_1_ID, TABLE_1_ID, request);
        });
    }

    @Test
    public void getAll_succeeds() {

        /* test */
    }

    @Test
    public void getAll_fails() {

        /* test */
    }

}
