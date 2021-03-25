package at.tuwien.mapper;

import at.tuwien.model.DatabaseContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;


public class ContainerToDatabaseContainerMapper {


    public DatabaseContainer map(InspectContainerResponse containerResponse) {
        DatabaseContainer databaseContainer = new DatabaseContainer();
        String ipAddress = containerResponse.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
        String dbName = Arrays.stream(containerResponse.getConfig().getEnv()).filter(s -> s.startsWith("POSTGRES_DB=")).findFirst().get();
        dbName = StringUtils.remove(dbName, "POSTGRES_DB=");
        databaseContainer.setContainerID(containerResponse.getId());
        databaseContainer.setContainerName(containerResponse.getName());
        databaseContainer.setCreated(containerResponse.getCreated());
        databaseContainer.setIpAddress(ipAddress);
        databaseContainer.setDbName(dbName);
        databaseContainer.setStatus(containerResponse.getState().getStatus());
        return databaseContainer;
    }
}
