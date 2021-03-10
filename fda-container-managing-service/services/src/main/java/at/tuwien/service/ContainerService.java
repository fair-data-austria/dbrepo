package at.tuwien.service;

import at.tuwien.api.dto.database.CreateDatabaseContainerDto;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.RestartPolicy;
import at.tuwien.entities.DatabaseContainer;
import at.tuwien.mapper.DatabaseContainerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContainerService {

    private final DockerClient dockerClient;
    private final DatabaseContainerMapper databaseContainerMapper;

    @Autowired
    public ContainerService(DockerClient dockerClient, DatabaseContainerMapper databaseContainerMapper) {
        this.dockerClient = dockerClient;
        this.databaseContainerMapper = databaseContainerMapper;
    }

    public String createDatabaseContainer(CreateDatabaseContainerDto dto) {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(8180, 8500);
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse(availableTcpPort + ":5432"))
                .withRestartPolicy(RestartPolicy.alwaysRestart());

//        CreateContainerResponse container = dockerClient.createContainerCmd("rdr-postgres:1.0")
//                .withName(dto.getContainerName())
//                .withEnv("POSTGRES_DB=" + dto.getDatabaseName(), "POSTGRES_PASSWORD=postgres")
//                .withHostConfig(hostConfig).exec();
//        dockerClient.startContainerCmd(container.getId()).exec();
//        return container.getId();
        return "deadbeef";
    }

    /**
     * Get specific information for container by id
     *
     * @param containerID The id
     * @return The specific information
     */
    public DatabaseContainer getDatabaseById(String containerID) {
        final InspectContainerResponse container = dockerClient.inspectContainerCmd(containerID).exec();
        return databaseContainerMapper.inspectContainerResponseToDatabaseContainer(container);
    }

    public List<DatabaseContainer> getAll() {
        final List<Container> containers = dockerClient.listContainersCmd()
                .withShowAll(true)
//                .withAncestorFilter(databaseProperties.getDatabaseImages())
                .exec();
        return containers.stream()
                .map(databaseContainerMapper::containerToDatabaseContainer)
                .collect(Collectors.toList());
    }


}
