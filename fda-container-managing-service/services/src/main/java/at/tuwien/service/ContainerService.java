package at.tuwien.service;

import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import java.time.Instant;
import java.util.List;
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
                            ImageRepository imageRepository, HostConfig hostConfig, ContainerMapper containerMapper, ImageMapper imageMapper) {
        this.hostConfig = hostConfig;
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.containerRepository = containerRepository;
        this.containerMapper = containerMapper;
        this.imageMapper = imageMapper;
    }

    public Container create(ContainerCreateRequestDto containerDto) throws ImageNotFoundException {
        final ContainerImage tmp = containerMapper.containerCreateRequestDtoToContainerImage(containerDto);
        final ContainerImage containerImage = imageRepository.findByRepositoryAndTag(tmp.getRepository(), tmp.getTag());
        if (containerImage == null) {
            log.error("failed to get image with name {}:{}", containerDto.getRepository(), containerDto.getTag());
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
        final Integer availableTcpPort = SocketUtils.findAvailableTcpPort(10000);
        final HostConfig hostConfig = this.hostConfig
                .withPortBindings(PortBinding.parse(availableTcpPort + ":" + containerImage.getDefaultPort()));
        final CreateContainerResponse response = dockerClient.createContainerCmd(containerMapper.containerCreateRequestDtoToDockerImage(containerDto))
                .withName(containerDto.getName())
                .withEnv(imageMapper.environmentItemsToStringList(containerImage.getEnvironment()))
                .withHostConfig(hostConfig)
                .exec();
        final Container container = Container.builder()
                .containerCreated(Instant.now())
                .image(containerImage)
                .name(containerDto.getName())
                .containerId(response.getId())
                .build();
        log.info("Created container with hash {}", container.getContainerId());
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
            dockerClient.stopContainerCmd(container.get().getContainerId()).exec();
        } catch (NotFoundException | NotModifiedException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
        log.debug("Stopped container {}", containerId);
        return container.get();
    }

    public void remove(Long containerId) throws ContainerNotFoundException, DockerClientException {
        final Optional<Container> container = containerRepository.findById(containerId);
        if (container.isEmpty()) {
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.removeContainerCmd(container.get().getContainerId()).exec();
        } catch (NotFoundException | NotModifiedException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
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
        final Container container = getById(containerId);
        try {
            dockerClient.startContainerCmd(container.getContainerId()).exec();
        } catch (NotFoundException | NotModifiedException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
        return container;
    }

}
