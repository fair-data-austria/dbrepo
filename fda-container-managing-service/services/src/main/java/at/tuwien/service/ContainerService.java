package at.tuwien.service;

import at.tuwien.api.dto.container.ContainerCreateRequestDto;
import at.tuwien.entity.Container;
import at.tuwien.entity.ContainerImage;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.ContainerMapper;
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
import java.util.ArrayList;
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

    @Autowired
    public ContainerService(DockerClient dockerClient, ContainerRepository containerRepository,
                            ImageRepository imageRepository, HostConfig hostConfig, ContainerMapper containerMapper) {
        this.hostConfig = hostConfig;
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.containerRepository = containerRepository;
        this.containerMapper = containerMapper;
    }

    public Container create(ContainerCreateRequestDto containerDto) throws ImageNotFoundException {
        final ContainerImage tmp = containerMapper.containerCreateRequestDtoToContainerImage(containerDto);
        final ContainerImage containerImage = imageRepository.findByRepositoryAndTag(tmp.getRepository(), tmp.getTag());
        if (containerImage == null) {
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
        final Integer availableTcpPort = SocketUtils.findAvailableTcpPort(10000);
        final HostConfig hostConfig = this.hostConfig
                .withPortBindings(PortBinding.parse(availableTcpPort + ":" + containerImage.getDefaultPort()));
        final List<String> environment = new ArrayList<>(containerImage.getEnvironment());
        final CreateContainerResponse response = dockerClient.createContainerCmd(containerDto.getImage())
                .withName(containerDto.getContainerName())
                .withEnv(environment)
                .withHostConfig(hostConfig)
                .exec();
        return Container.builder()
                .containerCreated(Instant.now())
                .image(containerImage)
                .name(containerDto.getContainerName())
                .containerId(response.getId())
                .build();
    }

    public Container stop(Long containerId) throws ContainerNotFoundException, DockerClientException {
        final Optional<Container> container = containerRepository.findById(containerId);
        if (container.isEmpty()) {
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.stopContainerCmd(container.get().getContainerId()).exec();
        } catch (NotFoundException | NotModifiedException e) {
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
            throw new DockerClientException("docker client failed", e);
        }
        log.debug("Removed container {}", containerId);
    }

    public Container getById(Long containerId) throws ContainerNotFoundException {
        final Optional<Container> container = containerRepository.findById(containerId);
        if (container.isEmpty()) {
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
            throw new DockerClientException("docker client failed", e);
        }
        return container;
    }

}
