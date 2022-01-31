package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.query.ExecuteStatementDto;
import at.tuwien.api.database.query.QueryResultDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.exception.*;
import at.tuwien.querystore.Query;
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
import java.sql.SQLException;
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
    public void findAll_succeeds() throws QueryStoreException, DatabaseNotFoundException, ImageNotSupportedException,
            InterruptedException, SQLException, ContainerNotFoundException {
        final QueryResultDto result = QueryResultDto.builder()
                .result(List.of(Map.of("key", "val")))
                .build();
        final ExecuteStatementDto statement1 = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();
        final ExecuteStatementDto statement2 = ExecuteStatementDto.builder()
                .statement(QUERY_2_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearQueryStore(TABLE_1);
        storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, statement1);
        storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, statement2);

        /* test */
        final List<Query> response = storeService.findAll(CONTAINER_1_ID, DATABASE_1_ID);
        assertEquals(2, response.size());
    }

    @Test
    public void findOne_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException,
            QueryNotFoundException, InterruptedException, SQLException, ContainerNotFoundException {
        final QueryResultDto result = QueryResultDto.builder()
                .result(List.of(Map.of("key", "val")))
                .build();
        final ExecuteStatementDto statement = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearQueryStore(TABLE_1);
        storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, statement);

        /* test */
        final Query response = storeService.findOne(CONTAINER_1_ID, DATABASE_1_ID, QUERY_1_ID);
        assertEquals(QUERY_1_ID, response.getId());
        assertEquals(QUERY_1_STATEMENT, response.getQuery());
        assertNotNull(response.getQueryHash());
    }

    @Test
    public void findOne_notFound_fails() throws InterruptedException, SQLException {
        final QueryResultDto result = QueryResultDto.builder()
                .result(List.of(Map.of("key", "val")))
                .build();
        final ExecuteStatementDto statement = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearQueryStore(TABLE_1);

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            storeService.findOne(CONTAINER_1_ID, DATABASE_1_ID, QUERY_1_ID);
        });
    }

    @Test
    public void insert_succeeds() throws DatabaseNotFoundException, ImageNotSupportedException, QueryStoreException,
            InterruptedException, SQLException, ContainerNotFoundException {
        final QueryResultDto result = QueryResultDto.builder()
                .result(List.of(Map.of("key", "val")))
                .build();
        final ExecuteStatementDto statement = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearQueryStore(TABLE_1);

        /* test */
        final Query response = storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, statement);
        assertEquals(QUERY_1_ID, response.getId());
        assertEquals(QUERY_1_STATEMENT, response.getQuery());
    }

    @Test
    public void insert_notRunning_fails() throws SQLException {
        final QueryResultDto result = QueryResultDto.builder()
                .result(List.of(Map.of("id", "1")))
                .build();
        final ExecuteStatementDto statement = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.stopContainer(CONTAINER_1);

        /* test */
        assertThrows(QueryStoreException.class, () -> {
            storeService.insert(CONTAINER_1_ID, DATABASE_1_ID, result, statement);
        });
    }

    @Test
    public void insert_dbNotFound_fails() throws InterruptedException, SQLException {
        final QueryResultDto result = QueryResultDto.builder()
                .result(List.of(Map.of("id", "1")))
                .build();
        final ExecuteStatementDto statement = ExecuteStatementDto.builder()
                .statement(QUERY_1_STATEMENT)
                .build();

        /* mock */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearQueryStore(TABLE_1);

        /* test */
        assertThrows(DatabaseNotFoundException.class, () -> {
            storeService.insert(CONTAINER_1_ID, 9999L, result, statement);
        });
    }

}
