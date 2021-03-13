package at.tuwien.service;

import at.tuwien.api.dto.database.CreateDatabaseContainerDto;
import at.tuwien.entity.ContainerImage;
import at.tuwien.entity.DatabaseContainer;
import at.tuwien.exception.ImageNotFoundException;
import at.tuwien.mapper.DatabaseContainerMapper;
import at.tuwien.repository.ContainerRepository;
import at.tuwien.repository.ImageRepository;
import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public DatabaseContainer getById(String containerId) {
        return containerRepository.findByContainerId(containerId);
    }

    public List<DatabaseContainer> getAll() {
        return containerRepository.findAll();
    }

}
