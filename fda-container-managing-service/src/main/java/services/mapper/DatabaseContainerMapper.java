package services.mapper;

import api.dto.container.DatabaseContainerBriefDto;
import api.dto.container.DatabaseContainerDto;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import services.entities.DatabaseContainer;

@Mapper(componentModel = "spring")
public interface DatabaseContainerMapper {

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

    DatabaseContainer containerToDatabaseContainer(Container data);

    DatabaseContainerBriefDto databaseContainerToDataBaseContainerBriefDto(DatabaseContainer data);

    DatabaseContainerDto databaseContainerToDataBaseContainerDto(DatabaseContainer data);
}
