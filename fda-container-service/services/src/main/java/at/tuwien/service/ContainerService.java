package at.tuwien.service;

import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.api.container.ContainerDto;
import at.tuwien.api.container.ContainerStateDto;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.ContainerStillRunningException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.PortBinding;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
public class ContainerService {

    private final HostConfig hostConfig;
    private final DockerClient dockerClient;
    private final ImageRepository imageRepository;
    private final ContainerRepository containerRepository;
    private final ContainerMapper containerMapper;
    private final ImageMapper imageMapper;

    @Autowired
    public ContainerService(DockerClient dockerClient, ContainerRepository containerRepository,
                            ImageRepository imageRepository, HostConfig hostConfig, ContainerMapper containerMapper,
                            ImageMapper imageMapper) {
        this.hostConfig = hostConfig;
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.containerRepository = containerRepository;
        this.containerMapper = containerMapper;
        this.imageMapper = imageMapper;
    }

    public Container create(ContainerCreateRequestDto createDto) throws ImageNotFoundException, DockerClientException {
        final Optional<ContainerImage> image = imageRepository.findByRepositoryAndTag(createDto.getRepository(), createDto.getTag());
        if (image.isEmpty()) {
            log.error("failed to get image with name {}:{}", createDto.getRepository(), createDto.getTag());
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
        final Integer availableTcpPort = SocketUtils.findAvailableTcpPort(10000);
        final HostConfig hostConfig = this.hostConfig
                .withNetworkMode("fda-userdb")
                .withLinks(List.of(new Link("fda-database-managing-service", "fda-database-managing-service")))
                .withPortBindings(PortBinding.parse(availableTcpPort + ":" + image.get().getDefaultPort()));
        /* save to metadata database */
        Container container = new Container();
        container.setContainerCreated(Instant.now());
        container.setImage(image.get());
        container.setPort(availableTcpPort);
        container.setName(createDto.getName());
        container.setInternalName(containerMapper.containerToInternalContainerName(container));
        /* create the container */
        final CreateContainerResponse response;
        try {
            response = dockerClient.createContainerCmd(containerMapper.containerCreateRequestDtoToDockerImage(createDto))
                    .withName(container.getInternalName())
                    .withHostName(container.getInternalName())
                    .withEnv(imageMapper.environmentItemsToStringList(image.get().getEnvironment()))
                    .withHostConfig(hostConfig)
                    .exec();
        } catch (ConflictException e) {
            log.error("conflicting names for container {}, reason: {}", createDto, e.getMessage());
            throw new DockerClientException("Unexpected behavior", e);
        }
        container.setHash(response.getId());
        container = containerRepository.save(container);
        log.info("Created container with hash {}", container.getHash());
        log.debug("container created {}", container);
        return container;
    }

    public Container stop(Long containerId) throws ContainerNotFoundException, DockerClientException {
        final Optional<Container> container = containerRepository.findById(containerId);
        if (container.isEmpty()) {
            log.error("failed to get container with id {}", containerId);
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.stopContainerCmd(container.get().getHash()).exec();
        } catch (NotFoundException | NotModifiedException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
        log.debug("Stopped container {}", container.get());
        return container.get();
    }

    public void remove(Long containerId) throws ContainerNotFoundException, DockerClientException,
            ContainerStillRunningException {
        final Optional<Container> container = containerRepository.findById(containerId);
        if (container.isEmpty()) {
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.removeContainerCmd(container.get().getHash()).exec();
        } catch (NotFoundException | NotModifiedException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        } catch (ConflictException e) {
            log.error("Could not remove container: {}", e.getMessage());
            throw new ContainerStillRunningException("docker client failed", e);
        }
        containerRepository.deleteById(containerId);
        log.debug("Removed container {}", containerId);
    }

    public Container getById(Long containerId) throws ContainerNotFoundException {
        final Optional<Container> container = containerRepository.findById(containerId);
        if (container.isEmpty()) {
            log.error("container with id {} does not exist", containerId);
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        return container.get();
    }

    public List<Container> getAll() {
        return containerRepository.findAll();
    }

    /**
     * Starts a database container by given container ID
     *
     * @param containerId The container ID
     * @return The container
     */
    public Container start(Long containerId) throws ContainerNotFoundException, DockerClientException {
        Container container = getById(containerId);
        try {
            dockerClient.startContainerCmd(container.getHash()).exec();
        } catch (NotFoundException | NotModifiedException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
        return container;
    }

    public Map<String, String> findIpAddresses(String containerHash) throws ContainerNotFoundException {
        final InspectContainerResponse response;
        try {
            response = dockerClient.inspectContainerCmd(containerHash)
                    .exec();
        } catch (NotFoundException e) {
            log.error("container {} not found", containerHash);
            throw new ContainerNotFoundException("container not found", e);
        }
        final Map<String, String> networks = new HashMap<>();
        response.getNetworkSettings()
                .getNetworks()
                .forEach((key, value) -> {
                    log.debug("network {} address {}", key, value);
                    networks.put(key, value.getIpAddress());
                });
        return networks;
    }

    public ContainerStateDto getContainerState(String containerHash) throws DockerClientException {
        final InspectContainerResponse response;
        try {
            response = dockerClient.inspectContainerCmd(containerHash)
                    .withSize(true)
                    .exec();
        } catch (NotFoundException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
        log.debug("received container state {}", response);
        final ContainerDto dto = containerMapper.inspectContainerResponseToContainerDto(response);
        return dto.getState();
    }

}
