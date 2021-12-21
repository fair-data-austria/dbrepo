package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.columns.ColumnCreateDto;
import at.tuwien.api.database.table.columns.ColumnTypeDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.config.MariaDbConfig;
import at.tuwien.config.ReadyConfig;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.TableServiceImpl;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.sql.SQLException;
import java.util.Arrays;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class TableServiceExperimentalIntegrationTest extends BaseUnitTest {

    @MockBean
    private Channel channel;

    @MockBean
    private ReadyConfig readyConfig;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private TableServiceImpl tableService;

    @BeforeAll
    public static void beforeAll() throws InterruptedException, SQLException {
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
        final CreateContainerResponse response = dockerClient.createContainerCmd(IMAGE_2_REPOSITORY + ":" + IMAGE_2_TAG)
                .withHostConfig(hostConfig.withNetworkMode("fda-userdb"))
                .withName(CONTAINER_1_INTERNALNAME)
                .withIpv4Address(CONTAINER_1_IP)
                .withHostName(CONTAINER_1_INTERNALNAME)
                .withEnv("MARIADB_USER=mariadb", "MARIADB_PASSWORD=mariadb", "MARIADB_ROOT_PASSWORD=mariadb", "MARIADB_DATABASE=weather")
                .withBinds(Bind.parse(bind))
                .exec();
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

    @Transactional
    @BeforeEach
    public void beforeEach() {
        TABLE_1.setDatabase(DATABASE_1);
        imageRepository.save(IMAGE_1);
        databaseRepository.save(DATABASE_1);
    }

    @Test
    public void createTable_succeeds() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException,
            ImageNotSupportedException, DataProcessingException, TableMalformedException, InterruptedException, SQLException {
        final TableCreateDto request = TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(COLUMNS_CSV01)
                .build();

        /* start */
        DockerConfig.startContainer(CONTAINER_1);
        MariaDbConfig.clearDatabase(TABLE_1);

        /* test */
        final Table response = tableService.createTable(DATABASE_1_ID, request);
        assertEquals(TABLE_2_NAME, response.getName());
        assertEquals(TABLE_2_INTERNALNAME, response.getInternalName());
        assertEquals(TABLE_2_DESCRIPTION, response.getDescription());
        assertEquals(DATABASE_1_ID, response.getTdbid());
        assertEquals(COLUMNS_CSV01.length, response.getColumns().size());
    }

}
