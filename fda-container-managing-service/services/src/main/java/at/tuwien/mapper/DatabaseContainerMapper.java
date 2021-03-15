package at.tuwien.mapper;

import at.tuwien.api.dto.container.DatabaseContainerBriefDto;
import at.tuwien.api.dto.container.DatabaseContainerDto;
import at.tuwien.api.dto.database.DatabaseContainerCreateResponseDto;
import at.tuwien.entity.ContainerImage;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.Container;
import at.tuwien.entity.DatabaseContainer;
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

    default ContainerImage imageToContainerImage(String image) {
        int index = image.indexOf(":");
        return new ContainerImage().builder()
                .repository(image.substring(0,index))
                .tag(image.substring(index+1))
                .build();
    }

    DatabaseContainer containerToDatabaseContainer(Container data);

    DatabaseContainerBriefDto databaseContainerToDataBaseContainerBriefDto(DatabaseContainer data);

    DatabaseContainerDto databaseContainerToDataBaseContainerDto(DatabaseContainer data);

    DatabaseContainerCreateResponseDto databaseContainerToCreateDatabaseResponseDto(DatabaseContainer data);
}
