package at.tuwien.service.impl;

import at.tuwien.api.container.ContainerCreateRequestDto;
import at.tuwien.config.MountProperties;
import at.tuwien.entities.container.Container;
import at.tuwien.entities.container.image.ContainerImage;
import at.tuwien.exception.*;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.mapper.ImageMapper;
import at.tuwien.repository.jpa.ContainerRepository;
import at.tuwien.repository.jpa.ImageRepository;
import at.tuwien.service.ContainerService;
import at.tuwien.service.UserService;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.ConflictException;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Link;
import com.github.dockerjava.api.model.PortBinding;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class ContainerServiceImpl implements ContainerService {

    private final HostConfig hostConfig;
    private final UserService userService;
    private final ImageMapper imageMapper;
    private final DockerClient dockerClient;
    private final MountProperties mountProperties;
    private final ImageRepository imageRepository;
    private final ContainerMapper containerMapper;
    private final ContainerRepository containerRepository;

    @Autowired
    public ContainerServiceImpl(DockerClient dockerClient, ContainerRepository containerRepository,
                                ImageRepository imageRepository, HostConfig hostConfig, UserService userService,
                                ContainerMapper containerMapper, ImageMapper imageMapper,
                                MountProperties mountProperties) {
        this.hostConfig = hostConfig;
        this.dockerClient = dockerClient;
        this.imageRepository = imageRepository;
        this.containerRepository = containerRepository;
        this.userService = userService;
        this.containerMapper = containerMapper;
        this.imageMapper = imageMapper;
        this.mountProperties = mountProperties;
    }

    @Override
    @Transactional
    public Container create(ContainerCreateRequestDto createDto) throws ImageNotFoundException,
            DockerClientException, UserNotFoundException {
        final Optional<ContainerImage> image = imageRepository.findByRepositoryAndTag(createDto.getRepository(),
                createDto.getTag());
        if (image.isEmpty()) {
            log.error("failed to get image with name {}:{}", createDto.getRepository(), createDto.getTag());
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
        final Integer availableTcpPort = SocketUtils.findAvailableTcpPort(10000);
        final HostConfig hostConfig = this.hostConfig
                .withNetworkMode("fda-userdb")
                .withLinks(List.of(new Link("fda-database-service", "fda-database-service")))
                .withPortBindings(PortBinding.parse(availableTcpPort + ":" + image.get().getDefaultPort()));
        /* user */
        final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        /* save to metadata database */
        Container container = new Container();
        container.setImage(image.get());
        container.setPort(availableTcpPort);
        container.setCreator(userService.findByUsername(authentication.getName()));
        container.setName(createDto.getName());
        container.setInternalName(containerMapper.containerToInternalContainerName(container));
        log.trace("will create host config {} and container {}", hostConfig, container);
        /* create the container */
        final CreateContainerResponse response;
        try {
            response = dockerClient.createContainerCmd(containerMapper.containerCreateRequestDtoToDockerImage(createDto))
                    .withName(container.getInternalName())
                    .withHostName(container.getInternalName())
                    .withEnv(imageMapper.environmentItemsToStringList(image.get().getEnvironment()))
                    .withHostConfig(hostConfig)
                    .withBinds(Bind.parse(mountProperties.getMountPath() + ":/tmp"))
                    .exec();
        } catch (ConflictException e) {
            log.error("Conflicting names {}", createDto.getName());
            throw new DockerClientException("Unexpected behavior", e);
        } catch (NotFoundException e) {
            log.error("The image {}:{} not available on the container service", createDto.getRepository(),
                    createDto.getTag());
            log.debug("payload was {}", createDto);
            throw new DockerClientException("Image not available", e);
        }
        container.setHash(response.getId());
        container = containerRepository.save(container);
        log.info("Created container {}", container.getId());
        log.debug("created container {}", container);
        return container;
    }

    @Override
    @Transactional(readOnly = true)
    public Container stop(Long containerId) throws ContainerNotFoundException, DockerClientException {
        final Container container = find(containerId);
        try {
            dockerClient.stopContainerCmd(container.getHash()).exec();
        } catch (NotFoundException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        } catch (NotModifiedException e) {
            log.warn("container already stopped {}", e.getMessage());
            throw new DockerClientException("container already stopped", e);
        }
        log.info("Stopped container with id {}", containerId);
        log.debug("stopped container {}", container);
        return container;
    }

    @Override
    @Transactional(readOnly = true)
    public void remove(Long containerId) throws ContainerNotFoundException, DockerClientException,
            ContainerStillRunningException {
        final Container container = find(containerId);
        try {
            dockerClient.removeContainerCmd(container.getHash()).exec();
        } catch (NotFoundException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        } catch (NotModifiedException e) {
            log.warn("container already removed {}", e.getMessage());
            throw new DockerClientException("container already removed", e);
        } catch (ConflictException e) {
            log.error("Could not remove container: {}", e.getMessage());
            throw new ContainerStillRunningException("docker client failed", e);
        }
        containerRepository.deleteById(containerId);
        log.info("Removed container with id {}", containerId);
        log.debug("removed container {}", container);
    }

    @Override
    @Transactional(readOnly = true)
    public Container find(Long id) throws ContainerNotFoundException {
        final Optional<Container> container = containerRepository.findById(id);
        if (container.isEmpty()) {
            log.error("failed to get container with id {}", id);
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        return container.get();
    }

    @Override
    @Transactional(readOnly = true)
    public Container inspect(Long id) throws ContainerNotFoundException, DockerClientException,
            ContainerNotRunningException {
        final Container container = find(id);
        final InspectContainerResponse response;
        try {
            response = dockerClient.inspectContainerCmd(container.getHash())
                    .withSize(true)
                    .exec();
        } catch (NotFoundException e) {
            log.error("Docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        }
        if (response.getState() == null || response.getState().getRunning() == null) {
            log.error("Docker state empty");
            log.debug("docker state empty {}", response);
            throw new DockerClientException("Docker state empty");
        }
        if (!response.getState().getRunning()) {
            throw new ContainerNotRunningException("container is not running");
        }
        /* now we only support one network */
        response.getNetworkSettings()
                .getNetworks()
                .forEach((key, network) -> {
                    log.trace("key {} network {}", key, network);
                    container.setIpAddress(network.getIpAddress());
                });
        log.trace("inspect container {}", container);
        return container;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Container> getAll() {
        final List<Container> containers = containerRepository.findAll();
        log.trace("found containers {}", containers);
        return containers;
    }

    @Override
    @Transactional(readOnly = true)
    public Container start(Long containerId) throws ContainerNotFoundException, DockerClientException {
        final Container container = find(containerId);
        try {
            dockerClient.startContainerCmd(container.getHash())
                    .exec();
        } catch (NotFoundException e) {
            log.error("docker client failed {}", e.getMessage());
            throw new DockerClientException("docker client failed", e);
        } catch (NotModifiedException e) {
            log.warn("container already started {}", e.getMessage());
            throw new DockerClientException("container already started", e);
        }
        log.info("Started container with id {}", containerId);
        log.debug("started container {}", container);
        return container;
    }

}
