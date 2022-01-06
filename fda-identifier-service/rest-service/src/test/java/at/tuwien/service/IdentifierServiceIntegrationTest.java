package at.tuwien.service;

import at.tuwien.BaseUnitTest;
import at.tuwien.api.identifier.CreatorDto;
import at.tuwien.api.identifier.IdentifierDto;
import at.tuwien.api.identifier.VisibilityTypeDto;
import at.tuwien.config.DockerConfig;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.identifier.Identifier;
import at.tuwien.exception.*;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.DatabaseRepository;
import at.tuwien.repository.jpa.IdentifierRepository;
import at.tuwien.repository.jpa.TableRepository;
import at.tuwien.service.impl.IdentifierServiceImpl;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Network;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static at.tuwien.config.DockerConfig.dockerClient;
import static at.tuwien.config.DockerConfig.hostConfig;
import static org.junit.jupiter.api.Assertions.*;

@Log4j2
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IdentifierServiceIntegrationTest extends BaseUnitTest {

    @Autowired
    private IdentifierServiceImpl identifierService;

    @Autowired
    private IdentifierRepository identifierRepository;

    @Autowired
    private DatabaseRepository databaseRepository;

    @Autowired
    private ContainerRepository containerRepository;

    private static Container GATEWAY = new Container();
    private static Container DISCOVERY = new Container();
    private static Container QUERY = new Container();
    private static Container METADATA_DB = new Container();

    @BeforeAll
    public static void beforeAll() {
        afterAll();
        /* create network */
        dockerClient.createNetworkCmd()
                .withName("fda-public")
                .withIpam(new Network.Ipam()
                        .withConfig(new Network.Ipam.Config()
                                .withSubnet("172.29.0.0/16")))
                .withEnableIpv6(false)
                .exec();
        /* create container */
        final CreateContainerResponse gateway = dockerClient.createContainerCmd(GATEWAY_SERVICE_REPOSITORY)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(GATEWAY_SERVICE_INTERNAL_NAME)
                .withIpv4Address(GATEWAY_SERVICE_IP)
                .withHostName(GATEWAY_SERVICE_INTERNAL_NAME)
                .withEnv(GATEWAY_SERVICE_ENV)
                .exec();
        final CreateContainerResponse discovery = dockerClient.createContainerCmd(DISCOVERY_SERVICE_REPOSITORY)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(DISCOVERY_SERVICE_INTERNAL_NAME)
                .withIpv4Address(DISCOVERY_SERVICE_IP)
                .withHostName(DISCOVERY_SERVICE_INTERNAL_NAME)
                .withEnv(DISCOVERY_SERVICE_ENV)
                .exec();
        final CreateContainerResponse query = dockerClient.createContainerCmd(QUERY_SERVICE_REPOSITORY)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(QUERY_SERVICE_INTERNAL_NAME)
                .withIpv4Address(QUERY_SERVICE_IP)
                .withHostName(QUERY_SERVICE_INTERNAL_NAME)
                .withEnv(QUERY_SERVICE_ENV)
                .exec();
        final CreateContainerResponse database = dockerClient.createContainerCmd(METADATA_DB_REPOSITORY)
                .withHostConfig(hostConfig.withNetworkMode("fda-public"))
                .withName(METADATA_DB_INTERNAL_NAME)
                .withIpv4Address(METADATA_DB_IP)
                .withHostName(METADATA_DB_INTERNAL_NAME)
                .withEnv(METADATA_DB_ENV)
                .exec();
        /* start */
        GATEWAY.setHash(gateway.getId());
        DISCOVERY.setHash(discovery.getId());
        QUERY.setHash(query.getId());
        METADATA_DB.setHash(database.getId());
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
        containerRepository.save(CONTAINER_1);
        databaseRepository.save(DATABASE_1);
        identifierRepository.save(IDENTIFIER_1);
    }

    @Test
    public void findAll_succeeds() {

        /* mock */
        identifierRepository.save(Identifier.builder()
                .id(IDENTIFIER_2_ID)
                .qid(IDENTIFIER_2_QUERY_ID)
                .dbid(IDENTIFIER_2_DATABASE_ID)
                .description(IDENTIFIER_2_DESCRIPTION)
                .title(IDENTIFIER_2_TITLE)
                .doi(IDENTIFIER_2_DOI)
                .visibility(IDENTIFIER_2_VISIBILITY)
                .created(IDENTIFIER_2_CREATED)
                .lastModified(IDENTIFIER_2_MODIFIED)
                .creators(List.of(CREATOR_1, CREATOR_2))
                .deleted(Instant.now().minus(4, ChronoUnit.MINUTES))
                .build());

        /* test */
        final List<Identifier> response = identifierService.findAll();
        assertEquals(1, response.size());
        assertEquals(IDENTIFIER_1, response.get(0));
    }

    @Test
    public void create_succeeds() throws IdentifierPublishingNotAllowedException, QueryNotFoundException,
            RemoteUnavailableException, IdentifierAlreadyExistsException, InterruptedException {

        /* mock */
        DockerConfig.startContainer(METADATA_DB);
        DockerConfig.startContainer(DISCOVERY);
        DockerConfig.startContainer(QUERY);
        DockerConfig.startContainer(GATEWAY);

        /* test */
        final Identifier response = identifierService.create(IDENTIFIER_2_DTO_REQUEST);
        assertEquals(IDENTIFIER_2, response);
    }

    @Test
    public void create_queryNotExists_fails() throws InterruptedException {
        final IdentifierDto request = IdentifierDto.builder()
                .qid(9999L)
                .dbid(IDENTIFIER_2_DATABASE_ID)
                .description(IDENTIFIER_2_DESCRIPTION)
                .title(IDENTIFIER_2_TITLE)
                .doi(IDENTIFIER_2_DOI)
                .visibility(IDENTIFIER_2_VISIBILITY_DTO)
                .created(IDENTIFIER_2_CREATED)
                .lastModified(IDENTIFIER_2_MODIFIED)
                .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
                .build();

        /* mock */
        DockerConfig.startContainer(METADATA_DB);
        DockerConfig.startContainer(DISCOVERY);
        DockerConfig.startContainer(QUERY);
        DockerConfig.startContainer(GATEWAY);

        /* test */
        assertThrows(QueryNotFoundException.class, () -> {
            identifierService.create(request);
        });
    }

    @Test
    public void create_identifierAlreadyExists_fails() throws InterruptedException {
        final IdentifierDto request = IdentifierDto.builder()
                .qid(IDENTIFIER_1_QUERY_ID)
                .dbid(IDENTIFIER_1_DATABASE_ID)
                .description(IDENTIFIER_2_DESCRIPTION)
                .title(IDENTIFIER_2_TITLE)
                .doi(IDENTIFIER_2_DOI)
                .visibility(IDENTIFIER_2_VISIBILITY_DTO)
                .created(IDENTIFIER_2_CREATED)
                .lastModified(IDENTIFIER_2_MODIFIED)
                .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
                .build();

        /* mock */
        DockerConfig.startContainer(METADATA_DB);
        DockerConfig.startContainer(DISCOVERY);
        DockerConfig.startContainer(QUERY);
        DockerConfig.startContainer(GATEWAY);

        /* test */
        assertThrows(IdentifierAlreadyExistsException.class, () -> {
            identifierService.create(request);
        });
    }

    @Test
    public void create_queryServiceUnavailable_fails() throws InterruptedException {

        /* mock */
        DockerConfig.startContainer(METADATA_DB);
        DockerConfig.startContainer(DISCOVERY);
        DockerConfig.startContainer(GATEWAY);
        DockerConfig.stopContainer(QUERY);

        /* test */
        assertThrows(RemoteUnavailableException.class, () -> {
            identifierService.create(IDENTIFIER_2_DTO_REQUEST);
        });
    }

    @Test
    public void find_succeeds() {
        fail();
    }

    @Test
    public void find_fails() {
        fail();
    }

    @Test
    public void update_fails() {
        fail();
    }

    @Test
    public void update_succeeds() throws IdentifierNotFoundException, IdentifierPublishingNotAllowedException {

        /* test */
        final Identifier response = identifierService.update(IDENTIFIER_1_ID, IDENTIFIER_1_DTO);
        assertEquals(response, IDENTIFIER_1);
    }

    @Test
    public void update_visibilityTrusted_fails() {
        final IdentifierDto request = IdentifierDto.builder()
                .id(IDENTIFIER_1_ID)
                .qid(IDENTIFIER_1_QUERY_ID)
                .dbid(IDENTIFIER_1_DATABASE_ID)
                .description(IDENTIFIER_1_DESCRIPTION)
                .title(IDENTIFIER_1_TITLE)
                .doi(IDENTIFIER_1_DOI)
                .visibility(VisibilityTypeDto.TRUSTED)
                .created(IDENTIFIER_1_CREATED)
                .lastModified(IDENTIFIER_1_MODIFIED)
                .creators(List.of(CREATOR_1_DTO, CREATOR_2_DTO).toArray(new CreatorDto[0]))
                .build();

        /* test */
        assertThrows(IdentifierPublishingNotAllowedException.class, () -> {
            identifierService.update(IDENTIFIER_1_ID, request);
        });
    }

    @Test
    public void publish_succeeds() {
        fail();
    }

    @Test
    public void publish_fails() {
        fail();
    }

    @Test
    public void delete_succeeds() {
        fail();
    }

    @Test
    public void delete_fails() {
        fail();
    }

}
