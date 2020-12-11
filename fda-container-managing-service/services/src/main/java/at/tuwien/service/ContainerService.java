package at.tuwien.service;

import at.tuwien.dto.CreateDatabaseContainerDTO;
import at.tuwien.mapper.ContainerToDatabaseContainerMapper;
import at.tuwien.model.DatabaseContainer;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ContainerService {

    private DockerClient dockerClient;

    @Autowired
    public ContainerService(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
    }

    public String createDatabaseContainer(CreateDatabaseContainerDTO dto) {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(8180, 8500);
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse(availableTcpPort + ":5432"))
                .withRestartPolicy(RestartPolicy.alwaysRestart());

        CreateContainerResponse container = dockerClient.createContainerCmd("rdr-postgres:1.0")
                .withName(dto.getContainerName())
                .withEnv("POSTGRES_DB=" + dto.getDbName(), "POSTGRES_PASSWORD=postgres")
                .withHostConfig(hostConfig).exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        return container.getId();
    }

    public DatabaseContainer getDatabaseContainerByContainerID(String containerID) {
        InspectContainerResponse container
                = dockerClient.inspectContainerCmd(containerID).exec();

        ContainerToDatabaseContainerMapper mapper = new ContainerToDatabaseContainerMapper();
        return mapper.map(container);
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
