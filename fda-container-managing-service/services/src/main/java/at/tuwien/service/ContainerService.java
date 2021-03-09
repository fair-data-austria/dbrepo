package at.tuwien.service;

import at.tuwien.dto.database.CreateDatabaseContainerDto;
import at.tuwien.mapper.ContainerMapper;
import at.tuwien.model.DatabaseContainer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.RestartPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContainerService {

    private final DockerClient dockerClient;
    private final ContainerMapper containerMapper;

    @Autowired
    public ContainerService(DockerClient dockerClient, ContainerMapper containerMapper) {
        this.dockerClient = dockerClient;
        this.containerMapper = containerMapper;
    }

    public String createDatabaseContainer(CreateDatabaseContainerDto dto) {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(8180, 8500);
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse(availableTcpPort + ":5432"))
                .withRestartPolicy(RestartPolicy.alwaysRestart());

        CreateContainerResponse container = dockerClient.createContainerCmd("rdr-postgres:1.0")
                .withName(dto.getContainerName())
                .withEnv("POSTGRES_DB=" + dto.getDatabaseName(), "POSTGRES_PASSWORD=postgres")
                .withHostConfig(hostConfig).exec();
        dockerClient.startContainerCmd(container.getId()).exec();
        return container.getId();
    }

    /**
     * Get specific information for container by id
     *
     * @param containerID The id
     * @return The specific information
     */
    public DatabaseContainer getDatabaseContainerByContainerID(String containerID) {
        final InspectContainerResponse container = dockerClient.inspectContainerCmd(containerID).exec();
        return containerMapper.inspectContainerResponseToDatabaseContainer(container);
    }

    public List<DatabaseContainer> findAllDatabaseContainers() {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).withAncestorFilter(Arrays.asList("rdr-postgres:1.0")).exec();
        List<DatabaseContainer> databaseContainers = new ArrayList<>();
        containers.forEach(container -> {
            DatabaseContainer databaseContainerByContainerByID = getDatabaseContainerByContainerID(container.getId());
            databaseContainers.add(databaseContainerByContainerByID);
        });

        return databaseContainers;
    }


}
