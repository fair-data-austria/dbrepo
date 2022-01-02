package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.Query;
import at.tuwien.exception.DatabaseNotFoundException;
import at.tuwien.exception.ImageNotSupportedException;
import at.tuwien.exception.QueryNotFoundException;
import at.tuwien.exception.QueryStoreException;
import at.tuwien.repository.jpa.TableRepository;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Network;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StoreServiceIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private StoreService storeService;

    @Autowired
    private TableRepository tableRepository;

    @BeforeAll
    public static void beforeAll() throws InterruptedException {
        afterAll();
        /* create network */
        dockerClient.createNetworkCmd()
                .withName("fda-userdb")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.28.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        /* create container */
        final String bind = new File("./src/test/resources/weather").toPath().toAbsolutePath() + ":/docker-entrypoint-initdb.d";
        log.trace("container bind {}", bind);
        final CreateContainerResponse response = dockerClient.createContainerCmd(IMAGE_1_REPOSITORY + ":" + IMAGE_1_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind))
                .exec();
        /* start */
        CONTAINER_1.setHash(response.getId());
        DockerConfig.startContainer(CONTAINER_1);
    }

    @AfterAll
    public static void afterAll() {
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

    @BeforeEach
    @Transactional
    public void beforeEach() {
        TABLE_1.setDatabase(DATABASE_1);
        TABLE_2.setDatabase(DATABASE_2);
        tableRepository.save(TABLE_1);
        tableRepository.save(TABLE_2);
    }

    @Test
    public void findAll_succeeds() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException {

        /* test */
        final List<Query> response = storeService.findAll(DATABASE_1_ID);
        assertEquals(1, response.size());
    }

    @Test
    public void findOne_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException,
            QueryNotFoundException {
        final QueryResultDto request = QueryResultDto.builder()
                .result(List.of(Map.of("key", "val")))
                .build();
        final ExecuteStatementDto query = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        storeService.insert(DATABASE_1_ID, request, query);

        /* test */
        final Query response = storeService.findOne(DATABASE_1_ID, QUERY_1_ID);
        assertEquals(QUERY_1_ID, response.getId());
        assertEquals(QUERY_1_STATEMENT, response.getQuery());
        assertNotNull(response.getQueryHash());
    }

    @Test
    public void findOne_notFound_fails() {

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            storeService.findOne(DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void insert_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException {
        final QueryResultDto request = QueryResultDto.builder()
                .result(List.of(Map.of("id", "1")))
                .build();
        final ExecuteStatementDto query = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* test */
        final Query response = storeService.insert(DATABASE_1_ID, request, query);
        assertEquals(QUERY_1_ID, response.getId());
        assertEquals(QUERY_1_STATEMENT, response.getQuery());
    }

    @Test
    public void insert_dbNotFound_fails() {
        final QueryResultDto request = QueryResultDto.builder()
                .result(List.of(Map.of("id", "1")))
                .build();
        final ExecuteStatementDto query = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            storeService.insert(9999L, request, query);
        });
    }

}
