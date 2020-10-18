package at.tuwien.service;

import at.tuwien.dto.CreateDatabaseConnectionDataDTO;
import at.tuwien.dto.CreateDatabaseContainerDTO;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.SocketUtils;

import java.util.Arrays;
import java.util.Optional;

@Service
public class ContainerService {
    @Autowired
    private DockerClient dockerClient;

    public String createDatabaseContainer(CreateDatabaseContainerDTO dto) {
        int availableTcpPort = SocketUtils.findAvailableTcpPort(8180, 8500);
        HostConfig hostConfig = HostConfig.newHostConfig()
                .withPortBindings(PortBinding.parse(availableTcpPort + ":5432"));

        CreateContainerResponse container = dockerClient.createContainerCmd("rdr-postgres:1.0")
                .withName(dto.getContainerName())
                .withEnv("POSTGRES_DB=" + dto.getDbName(), "POSTGRES_PASSWORD=postgres")
                .withHostConfig(hostConfig).exec();

        dockerClient.startContainerCmd(container.getId()).exec();

        return container.getId();
    }

    public CreateDatabaseConnectionDataDTO getContainerUrlByContainerID(String containerID) {
        InspectContainerResponse container
                = dockerClient.inspectContainerCmd(containerID).exec();

        String ipAddress = container.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
        String dbName = Arrays.stream(container.getConfig().getEnv()).filter(s -> s.startsWith("POSTGRES_DB=")).findFirst().get();
        dbName = StringUtils.remove(dbName, "POSTGRES_DB=");
        return new CreateDatabaseConnectionDataDTO(ipAddress, dbName);

    }

}
