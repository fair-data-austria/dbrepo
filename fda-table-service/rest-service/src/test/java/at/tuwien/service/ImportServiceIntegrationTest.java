package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.database.table.TableCreateDto;
import at.tuwien.api.database.table.TableInsertDto;
import at.tuwien.entities.database.table.Table;
import at.tuwien.exception.*;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.DatabaseRepository;
import at.tuwien.repository.ImageRepository;
import at.tuwien.repository.TableRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.synchronoss.cloud.nio.multipart.Multipart;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ImportServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private HostConfig hostConfig;

    @Autowired
    private DockerClient dockerClient;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ContainerRepository containerRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private DataService dataService;

    private CreateContainerResponse request;

    @Transactional
    @BeforeEach
    public void beforeEach() throws InterruptedException {
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
        CONTAINER_1_HASH = request.getId();
        databaseRepository.save(DATABASE_1);
        databaseRepository.save(DATABASE_2);
    }

    @Transactional
    @AfterEach
    public void afterEach() {
        /* stop containers and remove them */
        dockerClient.listContainersCmd()
                .withShowAll(true)
                .exec()
                .forEach(container -> {
                    System.out.println("DELETE CONTAINER " + Arrays.toString(container.getNames()));
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
                    System.out.println("DELETE NETWORK " + network.getName());
                    dockerClient.removeNetworkCmd(network.getId()).exec();
                });
    }

    private void create_table() throws ArbitraryPrimaryKeysException, DatabaseNotFoundException, ImageNotSupportedException, DataProcessingException {
        final Table response = dataService.createTable(DATABASE_1_ID, TableCreateDto.builder()
                .name(TABLE_2_NAME)
                .description(TABLE_2_DESCRIPTION)
                .columns(COLUMNS5)
                .build());
    }

    @Test
    public void insertFromFile_succeeds() throws TableNotFoundException, TableMalformedException,
            DatabaseNotFoundException, ImageNotSupportedException, FileStorageException,
            ArbitraryPrimaryKeysException, DataProcessingException {
        create_table();
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("classpath:weather-small.csv")
                .build();

        /* test */
        dataService.insertFromFile(DATABASE_1_ID, TABLE_2_ID, request);
        final Optional<Table> response = tableRepository.findByDatabaseAndId(DATABASE_1, TABLE_2_ID);
        assertTrue(response.isPresent());
        assertEquals(TABLE_2_ID, response.get().getId());
        assertEquals(TABLE_2_NAME, response.get().getName());
        assertEquals(TABLE_2_DESCRIPTION, response.get().getDescription());
    }

    @Test
    @Disabled
    public void insertFromFile_columnNumberDiffers_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException {
        create_table();
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("classpath:namen.csv")
                .build();

        /* test */
        assertThrows(FileStorageException.class, () -> {
            dataService.insertFromFile(DATABASE_1_ID, TABLE_2_ID, request);
        });
    }

    @Test
    public void insertFromFile_notRunning_fails() throws DatabaseNotFoundException, ImageNotSupportedException,
            ArbitraryPrimaryKeysException, DataProcessingException {
        create_table();
        dockerClient.stopContainerCmd(request.getId()).exec();
        final TableInsertDto request = TableInsertDto.builder()
                .delimiter(';')
                .skipHeader(true)
                .nullElement("NA")
                .csvLocation("classpath:weather-small.csv")
                .build();

        /* test */
        assertThrows(TableMalformedException.class, () -> {
            dataService.insertFromFile(DATABASE_1_ID, TABLE_2_ID, request);
        });
    }

}
