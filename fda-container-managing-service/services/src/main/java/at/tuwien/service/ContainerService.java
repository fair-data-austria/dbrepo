package at.tuwien.service;

import at.tuwien.api.dto.database.DatabaseContainerCreateDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.DatabaseContainer;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.DatabaseContainerMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Version;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class ContainerService {

    private final HostConfig hostConfig;
    private final DockerClient dockerClient;
    private final ImageRepository imageRepository;
    private final ContainerRepository containerRepository;
    private final DatabaseContainerMapper databaseContainerMapper;

    @Autowired
    public ContainerService(DockerClient dockerClient, ContainerRepository containerRepository,
                            ImageRepository imageRepository, HostConfig hostConfig, DatabaseContainerMapper databaseContainerMapper) {
        this.hostConfig = hostConfig;
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.containerRepository = containerRepository;
        this.databaseContainerMapper = databaseContainerMapper;
    }

    @PostConstruct
    public void version() {
        final Version version = dockerClient.versionCmd().exec();
        log.info("Docker {} API {} compiled for {}", version.getVersion(), version.getApiVersion(), version.getArch());
    }

    public DatabaseContainer create(DatabaseContainerCreateDto containerDto) throws ImageNotFoundException {
        if (containerDto == null || containerDto.getContainerName() == null || containerDto.getDatabaseName() == null
                || containerDto.getImage() == null) {
            throw new ImageNotFoundException("container data is null");
        }
        final int index = containerDto.getImage().indexOf(":");
        final String repositoryName = containerDto.getImage().substring(0, index);
        final String tagName = containerDto.getImage().substring(index + 1);
        final ContainerImage containerImage = imageRepository.findByImage(repositoryName, tagName);
        if (containerImage == null) {
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
        final Integer availableTcpPort = SocketUtils.findAvailableTcpPort(10000);
        final HostConfig hostConfig = this.hostConfig
                .withPortBindings(PortBinding.parse(availableTcpPort + ":" + containerImage.getDefaultPort()));
        final List<String> environment = new ArrayList<>(containerImage.getEnvironment());
        /* postgres specific env vars */
        if (repositoryName.equals("postgres")) {
            environment.add("POSTGRES_DB=" + containerDto.getDatabaseName());
        }
        final CreateContainerResponse response = dockerClient.createContainerCmd(containerDto.getImage())
                .withName(containerDto.getContainerName())
                .withEnv(environment)
                .withHostConfig(hostConfig)
                .exec();
        return DatabaseContainer.builder()
                .containerCreated(Instant.now())
                .image(containerImage)
                .name(containerDto.getContainerName())
                .databaseName(containerDto.getDatabaseName())
                .containerId(response.getId())
                .build();
    }

    public DatabaseContainer stop(String containerId) throws ContainerNotFoundException, DockerClientException {
        final DatabaseContainer container = containerRepository.findByContainerId(containerId);
        if (container == null) {
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch (NotFoundException | NotModifiedException e) {
            throw new DockerClientException("docker client failed", e);
        }
        log.debug("Stopped container {}", containerId);
        return container;
    }

    public DatabaseContainer remove(String containerId) throws ContainerNotFoundException, DockerClientException {
        final DatabaseContainer container = containerRepository.findByContainerId(containerId);
        if (container == null) {
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.removeContainerCmd(containerId).exec();
        } catch (NotFoundException | NotModifiedException e) {
            throw new DockerClientException("docker client failed", e);
        }
        log.debug("Removed container {}", containerId);
        return container;
    }

    public DatabaseContainer getById(String containerId) throws ContainerNotFoundException {
        final DatabaseContainer container = containerRepository.findByContainerId(containerId);
        if (container == null) {
            throw new ContainerNotFoundException("no database with this container id in metadata database");
        }
        return container;
    }

    public List<DatabaseContainer> getAll() {
        return containerRepository.findAll();
    }

    /**
     * Starts a database container by given container ID
     *
     * @param containerId The container ID
     * @return True if state changed
     */
    public boolean start(String containerId) throws ContainerNotFoundException, DockerClientException {
        final DatabaseContainer container = getById(containerId);
        try {
            dockerClient.startContainerCmd(containerId).exec();
        } catch (NotFoundException | NotModifiedException e) {
            throw new DockerClientException("docker client failed", e);
        }
        return true;
    }

}
