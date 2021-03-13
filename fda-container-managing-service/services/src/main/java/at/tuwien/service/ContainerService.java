package at.tuwien.service;

import at.tuwien.api.dto.database.CreateDatabaseContainerDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.DatabaseContainer;
import at.tuwien.exception.ContainerNotFoundException;
import at.tuwien.exception.DockerClientException;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.DatabaseContainerMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.exception.NotModifiedException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Log4j2
@Service
public class ContainerService {

    private final DockerClient dockerClient;
    private final ContainerRepository containerRepository;
    private final ImageRepository imageRepository;
    private final DatabaseContainerMapper databaseContainerMapper;

    @Autowired
    public ContainerService(DockerClient dockerClient, ContainerRepository containerRepository,
                            ImageRepository imageRepository, DatabaseContainerMapper databaseContainerMapper) {
        this.dockerClient = dockerClient;
        this.containerRepository = containerRepository;
        this.imageRepository = imageRepository;
        this.databaseContainerMapper = databaseContainerMapper;
    }

    public DatabaseContainer create(CreateDatabaseContainerDto containerDto) throws ImageNotFoundException {
        if (containerDto == null || containerDto.getContainerName() == null || containerDto.getDatabaseName() == null
                || containerDto.getImage() == null) {
            throw new ImageNotFoundException("container data is null");
        }
        final int index = containerDto.getImage().indexOf(":");
        final String repositoryName = containerDto.getImage().substring(0,index);
        final String tagName = containerDto.getImage().substring(index+1);
        final ContainerImage image = imageRepository.findByImage(repositoryName, tagName);
        if (image == null) {
            throw new ImageNotFoundException("image was not found in metadata database.");
        }
//        int availableTcpPort = SocketUtils.findAvailableTcpPort(8180, 8500);
//        HostConfig hostConfig = HostConfig.newHostConfig()
//                .withPortBindings(PortBinding.parse(availableTcpPort + ":5432"))
//                .withRestartPolicy(RestartPolicy.alwaysRestart());
//
////        CreateContainerResponse container = dockerClient.createContainerCmd("rdr-postgres:1.0")
////                .withName(dto.getContainerName())
////                .withEnv("POSTGRES_DB=" + dto.getDatabaseName(), "POSTGRES_PASSWORD=postgres")
////                .withHostConfig(hostConfig).exec();
////        dockerClient.startContainerCmd(container.getId()).exec();
////        return container.getId();
        return new DatabaseContainer();
    }

    public DatabaseContainer stop(String containerId) throws ContainerNotFoundException, DockerClientException {
        final DatabaseContainer container = containerRepository.findByContainerId(containerId);
        if (container == null) {
            throw new ContainerNotFoundException("no container with this id in metadata database");
        }
        try {
            dockerClient.stopContainerCmd(containerId).exec();
        } catch(NotFoundException | NotModifiedException e) {
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
        } catch(NotFoundException | NotModifiedException e) {
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
        final InspectContainerResponse response;
        try {
            response = dockerClient.inspectContainerCmd(containerId).exec();
        } catch(NotFoundException | NullPointerException e) {
            throw new ContainerNotFoundException("no database found with this id", e);
        }
        if (response != null && response.getState() != null && response.getState().getRunning()) {
            return false;
        }
        try {
            dockerClient.startContainerCmd(containerId).exec();
        } catch(NotFoundException | NotModifiedException e) {
            throw new DockerClientException("docker client failed", e);
        }
        return true;
    }

}
