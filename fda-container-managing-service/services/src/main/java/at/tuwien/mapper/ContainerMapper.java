package at.tuwien.mapper;

import at.tuwien.model.DatabaseContainer;
import com.github.dockerjava.api.command.InspectContainerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ContainerMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "name", target = "containerId"),
            @Mapping(source = "created", target = "created"),
            @Mapping(source = "ipAddress", target = "ipAddress"),
            @Mapping(source = "dbName", target = "dbName"),
    })
    DatabaseContainer inspectContainerResponseToDatabaseContainer(InspectContainerResponse containerResponse);
//    public DatabaseContainer map(InspectContainerResponse containerResponse) {
//        DatabaseContainer databaseContainer = new DatabaseContainer();
//        String ipAddress = containerResponse.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
//        String dbName = Arrays.stream(containerResponse.getConfig().getEnv()).filter(s -> s.startsWith("POSTGRES_DB=")).findFirst().get();
//        databaseContainer.setStatus(containerResponse.getState().getStatus());
//        return databaseContainer;
//    }
}
