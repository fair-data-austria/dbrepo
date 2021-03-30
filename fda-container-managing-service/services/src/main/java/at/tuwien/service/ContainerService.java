package at.tuwien.service;

import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.ContainerState;
import at.tuwien.exception.ContainerNotFoundException;
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
import com.github.dockerjava.api.model.*;
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
        final ContainerImage tmp = containerMapper.containerCreateRequestDtoToContainerImage(createDto);
        final ContainerImage containerImage = imageRepository.findByRepositoryAndTag(tmp.getRepository(), tmp.getTag());
        if (containerImage == null) {
            log.error("failed to get image with name {}:{}", createDto.getRepository(), createDto.getTag());
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
        final Integer availableTcpPort = SocketUtils.findAvailableTcpPort(10000);
        final HostConfig hostConfig = this.hostConfig
                .withPortBindings(PortBinding.parse(availableTcpPort + ":" + containerImage.getDefaultPort()));
        final CreateContainerResponse response;
        createDto.setName("fda-userdb-" + createDto.getName());
        try {
            response = dockerClient.createContainerCmd(containerMapper.containerCreateRequestDtoToDockerImage(createDto))
                    .withName(createDto.getName())
                    .withNetworkDisabled(false)
                    .withHostName(createDto.getName())
                    .withEnv(imageMapper.environmentItemsToStringList(containerImage.getEnvironment()))
                    .withHostConfig(hostConfig)
                    .exec();
        } catch (ConflictException e) {
            log.error("conflicting names for container {}, reason: {}", createDto, e.getMessage());
            throw new DockerClientException("Unexpected behavior", e);
        }
        /* connect to network */
        containerConnect(response.getId());
        /* save to metadata database */
        Container container = new Container();
        container.setContainerCreated(Instant.now());
        container.setImage(containerImage);
        container.setName(createDto.getName());
        container.setHash(response.getId());
        container.setPort(availableTcpPort);
        container.setStatus(ContainerState.CREATED);
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
        Container container1 = container.get();
        container1.setStatus(ContainerState.DEAD);
        container1 = containerRepository.save(container1);
        log.debug("Stopped container {}", container1);
        return container1;
    }

    public void remove(Long containerId) throws ContainerNotFoundException, DockerClientException {
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
            throw new DockerClientException("docker client failed", e);
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
        container.setStatus(ContainerState.RESTARTING);
        container = containerRepository.save(container);
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

    private void containerConnect(String containerHash) {
        final List<Network> networks = dockerClient.listNetworksCmd()
                .withNameFilter("fda-userdb")
                .exec();
        log.debug("docker networks discovered: {}", networks);
        dockerClient.connectToNetworkCmd()
                .withContainerId(containerHash)
                .withNetworkId(networks.get(0).getId())
                .withContainerNetwork(new ContainerNetwork()
                        .withIpamConfig(new ContainerNetwork.Ipam()))
                .exec();
    }

}
