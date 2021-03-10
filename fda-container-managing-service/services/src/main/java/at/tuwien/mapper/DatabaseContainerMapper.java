package at.tuwien.mapper;

import at.tuwien.api.dto.container.DatabaseContainerBriefDto;
import at.tuwien.api.dto.container.DatabaseContainerDto;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import at.tuwien.entities.DatabaseContainer;
import com.github.dockerjava.api.model.NetworkSettings;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface DatabaseContainerMapper {

    @Mappings({
            @Mapping(source = "id", target = "containerId"),
            @Mapping(source = "created", target = "containerCreated"),
    })
    DatabaseContainer inspectContainerResponseToDatabaseContainer(InspectContainerResponse containerResponse);
//    public DatabaseContainer map(InspectContainerResponse containerResponse) {
//        DatabaseContainer databaseContainer = new DatabaseContainer();
//        String ipAddress = containerResponse.getNetworkSettings().getNetworks().get("bridge").getIpAddress();
//        String dbName = Arrays.stream(containerResponse.getConfig().getEnv()).filter(s -> s.startsWith("POSTGRES_DB=")).findFirst().get();
//        databaseContainer.setStatus(containerResponse.getState().getStatus());
//        return databaseContainer;
//    }

    default String networkSettingsNetworksBridgeToIpAddress(NetworkSettings data) {
        return data.getNetworks().get("bridge").getIpAddress();
    }

    DatabaseContainer containerToDatabaseContainer(Container data);

    DatabaseContainerBriefDto databaseContainerToDataBaseContainerBriefDto(DatabaseContainer data);

    DatabaseContainerDto databaseContainerToDataBaseContainerDto(DatabaseContainer data);
}
